package com.scarlet.coroutines.basics

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import java.lang.RuntimeException

object Nested_Coroutines {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        log("Top-Level Coroutine")

        launch {
            log("Level 1 Coroutine")

            launch {
                log("Level 2 Coroutine")

                launch { log("Level 3 Coroutine") }
                launch { log("Level 3 Another Coroutine") }
            }
        }
    }
}

/**
 * Structured Concurrency Preview
 */

object Canceling_parent_coroutine_cancels_the_parent_and_its_children {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parent = launch {
            val child1 = launch {
                log("child1 started")
                delay(1000)
                log("child1 done")
            }
            val child2 = launch {
                log("child2 started")
                delay(1000)
                log("child2 done")
            }

            log("parent is waiting")
            joinAll(child1, child2)
            log("parent done")
        }

        parent.join()
//        delay(500)
//        parent.cancel() // parent.cancelAndJoin()

        log("Done")
    }
}

object Canceling_a_child_cancels_only_the_child {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var child1: Job? = null

        val parent = launch {
            child1 = launch {
                log("child1 started")
                delay(1000)
                log("child1 done")
            }
            val child2 = launch {
                log("child2 started")
                delay(1000)
                log("child2 done")
            }

            log("parent is waiting")
            joinAll(child1!!, child2)
            log("parent done")
        }

        delay(500)

        child1?.cancel()
        parent.join()

        log("Done")
    }
}

object Failed_parent_causes_cancellation_of_all_children {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parent = launch {
            launch {
                log("child1 started")
                delay(1000)
                log("child1 done")
            }

            launch {
                log("child2 started")
                delay(1000)
                log("child2 done")
            }

            delay(500)
            throw RuntimeException("parent failed")
        }

        parent.join()

        log("Done.")
    }
}

object Failed_child_causes_cancellation_of_its_parent_and_siblings {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parent = launch {
            val child1 = launch {
                log("child1 started")
                delay(500)
                throw RuntimeException("child 1 failed")
            }

            val child2 = launch {
                log("child2 started")
                delay(1000)
                log("child2 done")
            }

            log("parent is waiting")
            joinAll(child1, child2)
            log("parent done")
        }

        parent.join()

        log("Done.")
    }
}

