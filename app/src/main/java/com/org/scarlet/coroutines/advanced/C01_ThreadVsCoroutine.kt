package com.org.scarlet.coroutines.advanced

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

object Threads {
    @JvmStatic
    fun main(args: Array<String>) {
        // It does not crash on my machine!
        println(measureTimeMillis {
            repeat(200_000) {
                thread {
                    print(".")
                }
            }
        })
    }
}

object Coroutines {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        println(measureTimeMillis {
            repeat(200_000) {
                launch {
                    print(".")
                }
            }
        })
    }
}

