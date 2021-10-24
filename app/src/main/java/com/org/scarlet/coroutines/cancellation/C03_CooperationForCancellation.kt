package com.org.scarlet.coroutines.cancellation

import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.random.Random

object Uncooperative_Cancellation {

    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (true) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm working..")
                nextPrintTime += 500
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }

        delay(1500)

        job.cancelAndJoin()
    }
}

/**
 * CoroutineScope.{isActive, ensureActive()} and delay()
 *
 * Think about how to handle cleanup?
 */
object Cooperative_Cancellation {

    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (true) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("I'm working..")
                nextPrintTime += 500
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }

        delay(1500)

        job.cancelAndJoin()
    }
}

object Cleanup_When_Cancelled {

    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (isActive) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                println("job: I'm working..")
                nextPrintTime += 500
            }
        }

        if (!isActive) {
            withContext(NonCancellable) {
                println("Start cleanup ...")
                delay(500)
                println("Done cleanup ...")
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }

        delay(1500)

        job.cancelAndJoin()
    }
}


