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