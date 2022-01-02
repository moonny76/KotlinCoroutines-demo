package com.scarlet.coroutines.cancellation

import kotlinx.coroutines.*
import java.lang.Exception

/**
 * If Job is already in a "Cancelling" state, then suspension or starting
 * another coroutine is not possible at all.
 *
 * If we try to start another coroutine, it will just be ignored.
 *
 * If we try to suspend, it will throw CancellationException and our finally block will end.
 */

object Try_Launch_Or_Call_Suspending_Function_in_Cancelling_State {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val job = launch {
            try {
                delay(200)
                println("Job is done")
            } finally {
                println("Finally")

                println("isActive = ${coroutineContext.isActive}")
                println("isCancelled = ${coroutineContext[Job]?.isCancelled}")

                // Try to launch new coroutine
                launch { // will be ignored
                    println("Will not be printed")
                } //.join() // will throw cancellation exception

                // Try to call suspending function will throw cancellation exception
                try {
                    delay(100)
                    println("Will not be printed")
                } catch (ex: Exception) {
                    println("Caught: $ex")
                }
                // Nevertheless, if you want to call suspending function to clean up ... how to do?
            }
        }

        delay(100)
        job.cancelAndJoin()
        println("Cancel done")
    }
}

object Call_Suspending_Function_in_Cancelling_State_To_Cleanup {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val job = launch {
            try {
                delay(200)
                println("Coroutine finished")
            } finally {
                println("Finally")

                // DO NOT USE NonCancellable with launch or async
                withContext(NonCancellable) {
                    delay(1000L)
                    println("Cleanup done")
                }
            }
        }

        delay(100)
        job.cancelAndJoin()
        println("Cancel done")
    }
}

