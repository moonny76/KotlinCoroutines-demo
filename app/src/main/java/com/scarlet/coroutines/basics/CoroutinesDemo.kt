package com.scarlet.coroutines.basics

import com.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

object Generator {

    private fun fib(): Sequence<Int> = sequence {
        var x = 0
        var y = 1
        while (true) {
            println("${spaces(4)}Generates $x and waiting for next request")
            yield(x)
            x = y.also {
                y += x
            }
        }
    }

    private fun prompt(): Boolean {
        print("next? ")
        return !readLine().equals("n")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val iterator = fib().iterator()

        while (prompt()) {
            println("requesting ...")
            println("Got result = ${iterator.next()}")
        }
    }
}

object Coroutines_PingPong {

    private suspend fun ping() {
        while (coroutineContext.isActive) {
            println("ping")
            yield()
        }
    }

    private suspend fun pong() {
        while (coroutineContext.isActive) {
            println("pong")
            yield()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job1 = launch { ping() }
        val job2 = launch { pong() }

        delay(10)
        job1.cancelAndJoin()
        job2.cancelAndJoin()
    }
}

object Coroutines_Multitasking {

    private suspend fun coroutine1() {
        for (i in 1..10) {
            println("${spaces(4)}Coroutine 1: $i")
            yield()
        }
    }

    private suspend fun coroutine2() {
        for (i in 1..10) {
            println("${spaces(8)}Coroutine 2: $i")
            yield()
        }
    }

    private suspend fun coroutine3() {
        for (i in 1..10) {
            println("${spaces(12)}Coroutine 3: $i")
            yield()
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job1 = launch { coroutine1() }
        val job2 = launch { coroutine2() }
        val job3 = launch { coroutine3() }

        joinAll(job1, job2, job3)
        println("Done!")
    }
}
