package com.scarlet.coroutines.basics

import com.scarlet.util.spaces
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

object Generator {

    fun fib(): Sequence<Int> = sequence {
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

    private fun prompt(msg: String): Boolean {
        print(msg)
        return !readLine().equals("n")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val iterator = fib().iterator()

        while (prompt("next? ")) {
            println("requesting ...")
            println("Got result = ${iterator.next()}")
        }
    }
}

object Coroutines_PingPong {

    suspend fun ping() {
        while (coroutineContext.isActive) {
            println("ping")
            yield()
        }
    }

    suspend fun pong() {
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

    suspend fun coroutine1() {
        for (i in 1..10) {
            println("${spaces(4)}Coroutine 1: $i")
            yield()
        }
    }

    suspend fun coroutine2() {
        for (i in 1..10) {
            println("${spaces(8)}Coroutine 2: $i")
            yield()
        }
    }

    suspend fun coroutine3() {
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
