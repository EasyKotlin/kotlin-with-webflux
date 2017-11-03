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