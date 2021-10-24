package com.org.scarlet.coroutines.basics

import java.lang.Thread.sleep

fun fib(): Sequence<Int> = sequence {
    var x = 0
    var y = 1
    while (true) {
//        println("\t\t\t\tgenerates $x and waiting for next request")
        yield(x)
        x = y.also {
            y += x
        }
    }
}

fun main() {
    val iterator = fib().iterator()

    repeat(10) {
        print("next? ")
        println("${iterator.next()}")
        sleep(2000)
    }
}
