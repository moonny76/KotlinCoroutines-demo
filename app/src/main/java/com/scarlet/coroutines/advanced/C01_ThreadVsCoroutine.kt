package com.scarlet.coroutines.advanced

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

object Threads {
    @JvmStatic
    fun main(args: Array<String>) {
        val time = measureTimeMillis {
            val jobs = List(100_000) {
                thread {
                    Thread.sleep(1_000)
                    print(".")
                }
            }
            jobs.forEach { it.join() }
        }
        println("\nElapses time = $time ms")
    }
}

object Coroutines {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val time = measureTimeMillis {
            val jobs = List(100_000) {
                launch {
                    delay(1_000)
                    print(".")
                }
            }
            jobs.forEach { it.join() }
        }
        println("\nElapses time = $time ms")
    }
}

