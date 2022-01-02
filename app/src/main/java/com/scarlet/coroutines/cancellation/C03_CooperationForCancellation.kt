package com.scarlet.coroutines.cancellation

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*

object Uncooperative_Cancellation {

    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (true) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                log("I'm working..")
                nextPrintTime += 500
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }.onCompletion("job")

        delay(1500)

        log("Try to cancel the job ...")
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
        while (isActive) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                log("I'm working..")
                nextPrintTime += 500
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }.onCompletion("job")

        delay(1500)

        log("Try to cancel the job ...")
        job.cancelAndJoin()
    }
}

object Cleanup_When_Cancelled {

    private suspend fun printTwice() = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        var nextPrintTime = startTime
        while (isActive) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                log("job: I'm working..")
                nextPrintTime += 500
            }
        }

        if (!isActive) {
            withContext(NonCancellable) {
                log("Start cleanup ...")
                delay(500)
                log("Done cleanup ...")
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            printTwice()
        }

        delay(1500)

        log("Try to cancel the job ...")
        job.cancelAndJoin()
    }
}


