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