package com.scarlet.coroutines.advanced

import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

internal fun log(msg: String) {
    println("[${Thread.currentThread().name}] $msg")
}

object Phase1 {
    private fun fooWithDelay(a: Int, b: Int): Int {
        log("step 1")
        Thread.sleep(1000)
        log("step 2")
        return a + b
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("main started")

        log("result = ${fooWithDelay(3, 4)}")

        log("main end")
    }
}

//object Phase2
val executor = Executors.newSingleThreadScheduledExecutor {
    Thread(it, "scheduler").apply { isDaemon = true }
}
