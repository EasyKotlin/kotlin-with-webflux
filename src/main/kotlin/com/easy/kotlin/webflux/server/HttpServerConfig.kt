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