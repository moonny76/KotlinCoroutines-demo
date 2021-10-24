package com.org.scarlet.coroutines.basics

import com.org.scarlet.util.logi
import kotlinx.coroutines.*
import java.lang.RuntimeException

object Nested_Coroutines {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {
        logi(0, "Level 0 Coroutine")

        launch {
            logi(1, "Level 1 Coroutine")
            launch {
                logi(2, "Level 2 Coroutine")
                launch {
                    logi(3, "Level 3 Coroutine")
                }
                launch {
                    logi(3, "Level 3 Coroutine")
                }
            }
        }
    }
}

/**
 * Structured Concurrency Preview
 */

object Canceling_parent_cancels_itself_and_all_children {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parent = launch {
            val child1 = launch {
                println("child1 started")
                delay(1000)
                println("child1 done")
            }
            val child2 = launch {
                println("child2 started")
                delay(1000)
                println("child2 done")
            }
            joinAll(child1, child2)
            println("parent done")
        }

        delay(500)

//        parent.cancel()
        parent.join()

        println("Done")
    }
}

object Canceling_a_child_cancels_only_the_child {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var child1: Job? = null
        var child2: Job? = null
        val parent = launch {
            child1 = launch {
                println("child1 started")
                delay(1000)
                println("child1 done")
            }
            child2 = launch {
                println("child2 started")
                delay(1000)
                println("child2 done")
            }
            joinAll(child1!!, child2!!)
            println("parent done")
        }

        delay(500)

        child1?.cancel()
        parent.join()

        println("Done")
    }
}

object Failed_child_cause_cancellation_of_its_parent_and_its_siblings {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var child1: Job? = null
        var child2: Job? = null

        val parent = launch {
            child1 = launch {
                println("child1 started")
                delay(500)
                throw RuntimeException("child 1 failed")
            }

            child2 = launch {
                println("child2 started")
                delay(1000)
                println("child2 done")
            }

            joinAll(child1!!, child2!!)
            println("parent done")
        }

        parent.join()

        println("Done.")
    }
}

object Failed_parent_cause_cancellation_of_all_its_siblings {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parent = launch {
            launch {
                println("child1 started")
                delay(1000)
                println("child1 done")
            }

            launch {
                println("child2 started")
                delay(1000)
                println("child2 done")
            }

            delay(500)
            throw RuntimeException("parent failed")
        }

        parent.join()

        println("Done.")
    }
}
