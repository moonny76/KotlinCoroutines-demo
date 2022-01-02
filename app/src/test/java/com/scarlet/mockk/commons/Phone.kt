package com.scarlet.mockk.commons

class Phone {
    fun call(person: Person) {
        println("Place phone call from ${person.name}")
    }
}