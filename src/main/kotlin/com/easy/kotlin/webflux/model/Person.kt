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