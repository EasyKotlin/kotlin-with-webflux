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