
# Kotlin 使用 Spring WebFlux 实现响应式编程

> kotlin-with-webflux

IBM的研究称，整个人类文明所获得的全部数据中，有90%是过去两年内产生的。在此背景下，包括NoSQL，Hadoop, Spark, Storm, Kylin在内的大批新技术应运而生。其中以RxJava和Reactor为代表的响应式（Reactive）编程技术针对的就是经典的大数据

>  4V定义: Volume，Variety，Velocity，Value）

中的Velocity，即高并发问题，而在即将发布的Spring 5中，也引入了响应式编程的支持。在接下来的博客文章中，我会围绕响应式编程相关的主题与你分享我的学习心得。作为第一篇，首先从Spring 5 和 Spring WebFlux 谈起。


##  响应式宣言


响应式宣言和敏捷宣言一样，说起响应式编程，必先提到响应式宣言。

> We want systems that are Responsive, Resilient, Elastic and Message Driven. We call these Reactive Systems. - The Reactive Manifesto



响应式宣言中也包含了4组关键词：

Responsive: 可响应的。要求系统尽可能做到在任何时候都能及时响应。
Resilient: 可恢复的。要求系统即使出错了，也能保持可响应性。
Elastic: 可伸缩的。要求系统在各种负载下都能保持可响应性。
Message Driven: 消息驱动的。要求系统通过异步消息连接各个组件。
可以看到，对于任何一个响应式系统，首先要保证的就是可响应性，否则就称不上是响应式系统。从这个意义上来说，动不动就蓝屏的Windows系统显然不是一个响应式系统。



##  Spring 5 响应式Web框架架构图

![](http://upload-images.jianshu.io/upload_images/1233356-2c462ca74b31f59b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

（https://docs.spring.io/spring-framework/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/html/web-reactive.html）



左侧是传统的基于Servlet的Spring Web MVC框架

右侧是5.0版本新引入的基于Reactive Streams的Spring WebFlux框架

从上到下依次是

- Router Functions
- WebFlux
- Reactive Streams

三个新组件。

##  Router Functions:

对标@Controller，@RequestMapping等标准的Spring MVC注解，提供一套函数式风格的API，用于创建Router，Handler和Filter。


##  WebFlux: 核心组件

协调上下游各个组件提供响应式编程支持。

##  Reactive Streams

一种支持背压（Backpressure）的异步数据流处理标准，主流实现有RxJava和Reactor，Spring WebFlux默认集成的是Reactor。


在Web容器的选择上，Spring WebFlux既支持像Tomcat，Jetty这样的的传统容器（前提是支持Servlet 3.1 Non-Blocking IO API），又支持像Netty，Undertow那样的异步容器。不管是何种容器，Spring WebFlux都会将其输入输出流适配成Flux<DataBuffer>格式，以便进行统一处理。

值得一提的是，除了新的Router Functions接口，Spring WebFlux同时支持使用老的Spring MVC注解声明Reactive Controller。和传统的MVC Controller不同，Reactive Controller操作的是非阻塞的ServerHttpRequest和ServerHttpResponse，而不再是Spring MVC里的HttpServletRequest和HttpServletResponse。



下面是示例工程详解。



##  示例工程详解


![螢幕快照 2017-11-03 22.24.39.png](http://upload-images.jianshu.io/upload_images/1233356-eb4cab6a422bccb9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![螢幕快照 2017-11-03 22.25.54.png](http://upload-images.jianshu.io/upload_images/1233356-a09726954d7a06c5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![螢幕快照 2017-11-03 22.26.04.png](http://upload-images.jianshu.io/upload_images/1233356-2c6647cac04b43a0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![螢幕快照 2017-11-03 22.26.14.png](http://upload-images.jianshu.io/upload_images/1233356-b96cb22a900e4fed.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![螢幕快照 2017-11-03 22.47.01.png](http://upload-images.jianshu.io/upload_images/1233356-a81bbb5d6ae8b424.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


工程目录结构

```
~/ak47/webflux$ tree
.
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── src
│   ├── main
│   │   ├── java
│   │   ├── kotlin
│   │   │   └── com
│   │   │       └── easy
│   │   │           └── kotlin
│   │   │               └── webflux
│   │   │                   └── WebfluxApplication.kt
│   │   └── resources
│   │       └── application.properties
│   └── test
│       ├── java
│       ├── kotlin
│       │   └── com
│       │       └── easy
│       │           └── kotlin
│       │               └── webflux
│       │                   └── WebfluxApplicationTests.kt
│       └── resources
└── webflux.iml

19 directories, 11 files

```


项目依赖配置

```groovy
buildscript {
	ext {
		kotlinVersion = '1.1.51'
		springBootVersion = '2.0.0.BUILD-SNAPSHOT'
	}
	repositories {
		mavenCentral()
		maven { url "https://repo.spring.io/snapshot" }
		maven { url "https://repo.spring.io/milestone" }
	}
	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
		classpath("org.jetbrains.kotlin:kotlin-allopen:${kotlinVersion}")
	}
}

apply plugin: 'kotlin'
apply plugin: 'kotlin-spring'
apply plugin: 'eclipse'
apply plugin: 'org.springframework.boot'
apply plugin: 'io.spring.dependency-management'

group = 'com.easy.kotlin'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = 1.8
compileKotlin {
	kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin {
	kotlinOptions.jvmTarget = "1.8"
}

repositories {
	mavenCentral()
	maven { url "https://repo.spring.io/snapshot" }
	maven { url "https://repo.spring.io/milestone" }
}


dependencies {
	compile('org.springframework.boot:spring-boot-starter-webflux')
	compile("org.jetbrains.kotlin:kotlin-stdlib-jre8")
	compile("org.jetbrains.kotlin:kotlin-reflect")
	testCompile('org.springframework.boot:spring-boot-starter-test')
	testCompile('io.projectreactor:reactor-test')
}

```


这是 Spring Initializr 帮我们自动生成的样板工程。下面我们分别来加入 Model 、dao 、 service 、 handler 等模块的内容。

源码目录结构设计如下
```
├── src
│   ├── main
│   │   ├── java
│   │   ├── kotlin
│   │   │   └── com
│   │   │       └── easy
│   │   │           └── kotlin
│   │   │               └── webflux
│   │   │                   ├── WebfluxApplication.kt
│   │   │                   ├── dao
│   │   │                   │   └── PersonRepository.kt
│   │   │                   ├── handler
│   │   │                   │   └── PersonHandler.kt
│   │   │                   ├── model
│   │   │                   │   └── Person.kt
│   │   │                   ├── router
│   │   │                   │   └── RouterConfig.kt
│   │   │                   ├── server
│   │   │                   │   └── HttpServerConfig.kt
│   │   │                   └── service
│   │   │                       └── PersonService.kt
│   │   └── resources
│   │       └── application.properties

```

Person

```kotlin
package com.easy.kotlin.webflux.model

import com.fasterxml.jackson.annotation.JsonProperty

class Person(@JsonProperty("name") val name: String, @JsonProperty("age") val age: Int) {

    override fun toString(): String {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}'
    }
}
```




PersonRepository

```kotlin
package com.easy.kotlin.webflux.dao

import com.easy.kotlin.webflux.model.Person
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface PersonRepository {

    fun getPerson(id: Int): Mono<Person>

    fun allPeople(): Flux<Person>

    fun savePerson(person: Mono<Person>): Mono<Void>
}


/*
 * Mono 和 Flux 是由 Project Reactor 提供的 Reactive 类型。
 * Springs 同时支持其他 Reactive 流实现，如 RXJava。
 * Mono 和 Flux 是由 Reactive 流的 Publisher 中实现的。
 * Mono 是一个用来发送 0 或者单值数据的发布器，
 * Flux 可以用来发送 0 到 N 个值。
 *
 * 这非常类似 Flowable 和 RxJava 中的 Observable 。它们表示在订阅这些发布服务时发送数值流。 */
```



PersonService

```kotlin
package com.easy.kotlin.webflux.service

import com.easy.kotlin.webflux.model.Person
import com.easy.kotlin.webflux.dao.PersonRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class PersonService : PersonRepository {
    var persons: MutableMap<Int, Person> = hashMapOf()

    constructor() {
        this.persons[1] = Person("Jack", 20)
        this.persons[2] = Person("Rose", 16)
    }


    override fun getPerson(id: Int): Mono<Person> {
        return Mono.justOrEmpty(this.persons[id])
    }

    override fun allPeople(): Flux<Person> {
        return Flux.fromIterable(this.persons.values)
    }

    override fun savePerson(person: Mono<Person>): Mono<Void> {
        return person.doOnNext {
            val id = this.persons.size + 1
            persons.put(id, it)
            println("Saved ${person} with ${id}")
        }.thenEmpty(Mono.empty())

    }
}
```



PersonHandler

```kotlin
package com.easy.kotlin.webflux.handler

import com.easy.kotlin.webflux.dao.PersonRepository
import com.easy.kotlin.webflux.model.Person
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters.fromObject


@Service
class PersonHandler {

    @Autowired lateinit var repository: PersonRepository


    fun getPerson(request: ServerRequest): Mono<ServerResponse> {
        val personId = Integer.valueOf(request.pathVariable("id"))!!
        val notFound = ServerResponse.notFound().build()
        val personMono = this.repository.getPerson(personId)
        return personMono
            .flatMap { person -> ServerResponse.ok().contentType(APPLICATION_JSON).body(fromObject(person)) }
            .switchIfEmpty(notFound)
    }


    fun createPerson(request: ServerRequest): Mono<ServerResponse> {
        val person = request.bodyToMono(Person::class.java)
        return ServerResponse.ok().build(this.repository.savePerson(person))
    }

    fun listPeople(request: ServerRequest): Mono<ServerResponse> {
        val people = this.repository.allPeople()
        return ServerResponse.ok().contentType(APPLICATION_JSON).body(people, Person::class.java)
    }


}
```



RouterConfig

```kotlin
package com.easy.kotlin.webflux.router


import com.easy.kotlin.webflux.handler.PersonHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route


@Configuration
class RouterConfig {

    @Autowired lateinit var personHandler: PersonHandler

    @Bean
    fun routerFunction(): RouterFunction<*> {
        return route(GET("/api/person").and(accept(APPLICATION_JSON)),
                HandlerFunction { personHandler.listPeople(it) })

            .and(route(GET("/api/person/{id}").and(accept(APPLICATION_JSON)),
                    HandlerFunction { personHandler.getPerson(it) }))
    }

}
```




HttpServerConfig

```kotlin
package com.easy.kotlin.webflux.server

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import reactor.ipc.netty.http.server.HttpServer


@Configuration
class HttpServerConfig {
    @Autowired
    lateinit var environment: Environment

    @Bean
    fun httpServer(routerFunction: RouterFunction<*>): HttpServer {
        val httpHandler = RouterFunctions.toHttpHandler(routerFunction)
        val adapter = ReactorHttpHandlerAdapter(httpHandler)
        val server = HttpServer.create("localhost", environment.getProperty("server.port").toInt())
        server.newHandler(adapter)
        return server
    }

}
```



项目启动入口类 WebfluxApplication

```kotlin

package com.easy.kotlin.webflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebfluxApplication

fun main(args: Array<String>) {
    runApplication<WebfluxApplication>(*args)
}

```



启动运行


![螢幕快照 2017-11-04 00.40.18.png](http://upload-images.jianshu.io/upload_images/1233356-15d47675b59dbbee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)




注意到这行：

```
Mapped ((GET && /api/person) && Accept: [application/json]) -> com.easy.kotlin.webflux.router.RouterConfig$routerFunction$1@46292372
((GET && /api/person/{id}) && Accept: [application/json]) -> com.easy.kotlin.webflux.router.RouterConfig$routerFunction$2@126be319
```


完整启动日志
```

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::  (v2.0.0.BUILD-SNAPSHOT)

2017-11-04 00:39:46.046  INFO 2884 --- [           main] c.e.kotlin.webflux.WebfluxApplicationKt  : Starting WebfluxApplicationKt on jacks-MacBook-Air.local with PID 2884 (/Users/jack/ak47/webflux/out/production/classes started by jack in /Users/jack/ak47/webflux)
2017-11-04 00:39:46.077  INFO 2884 --- [           main] c.e.kotlin.webflux.WebfluxApplicationKt  : No active profile set, falling back to default profiles: default
2017-11-04 00:39:46.247  INFO 2884 --- [           main] .r.c.ReactiveWebServerApplicationContext : Refreshing org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext@4b0b0854: startup date [Sat Nov 04 00:39:46 CST 2017]; root of context hierarchy
2017-11-04 00:39:48.995  INFO 2884 --- [           main] o.s.w.r.f.s.s.RouterFunctionMapping      : Mapped ((GET && /api/person) && Accept: [application/json]) -> com.easy.kotlin.webflux.router.RouterConfig$routerFunction$1@46292372
((GET && /api/person/{id}) && Accept: [application/json]) -> com.easy.kotlin.webflux.router.RouterConfig$routerFunction$2@126be319
2017-11-04 00:39:49.017  INFO 2884 --- [           main] o.s.w.r.handler.SimpleUrlHandlerMapping  : Mapped URL path [/webjars/**] onto handler of type [class org.springframework.web.reactive.resource.ResourceWebHandler]
2017-11-04 00:39:49.017  INFO 2884 --- [           main] o.s.w.r.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**] onto handler of type [class org.springframework.web.reactive.resource.ResourceWebHandler]
2017-11-04 00:39:49.215  INFO 2884 --- [           main] o.s.w.r.r.m.a.ControllerMethodResolver   : Looking for @ControllerAdvice: org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext@4b0b0854: startup date [Sat Nov 04 00:39:46 CST 2017]; root of context hierarchy
2017-11-04 00:39:50.309  INFO 2884 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2017-11-04 00:39:50.459  INFO 2884 --- [ctor-http-nio-1] r.ipc.netty.tcp.BlockingNettyContext     : Started HttpServer on /0:0:0:0:0:0:0:0:9000
2017-11-04 00:39:50.459  INFO 2884 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port(s): 9000
2017-11-04 00:39:50.466  INFO 2884 --- [           main] c.e.kotlin.webflux.WebfluxApplicationKt  : Started WebfluxApplicationKt in 5.047 seconds (JVM running for 6.276)

```

## 测试输出


```json

$ curl http://127.0.0.1:9000/api/person
[{"name":"Jack","age":20},{"name":"Rose","age":16}]


$ curl http://127.0.0.1:9000/api/person/1
{"name":"Jack","age":20}



$ curl http://127.0.0.1:9000/api/person/2
{"name":"Rose","age":16}

```





##  小结

Spring Web 是一个命令式的编程框架，可以很方便的进行开发和调试。你需要根据实际情况去决定采用 Spring 5 Reactive 或者是 Spring Web 命令式框架。在很多情况下，命令式的编程风格就可以满足，但当你的应用需要高可伸缩性，那么 Reactive 非堵塞方式是最适合的。


本章工程源代码：https://github.com/EasyKotlin/kotlin-with-webflux


参考资料
===

Spring Framework 5.0 M5 Update ： https://spring.io/blog/2017/02/23/spring-framework-5-0-m5-update


https://github.com/poutsma/web-function-sample






