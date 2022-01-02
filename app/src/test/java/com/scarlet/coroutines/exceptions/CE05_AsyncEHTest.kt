package com.scarlet.coroutines.exceptions

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CE05_AsyncEHTest {

    @Test
    fun `try-catch inside async`() = runTest {

        val deferred = async {
            try {
                throw RuntimeException("my exception")
                42
            } catch (ex: Exception) {
                println("Caught: $ex") // caught
            }
        }

        println(deferred.await())
    }

    /**
     * Root coroutine cases
     */

    @Test
    fun `root coroutine - direct child of scope - normal exception handling behavior`() =
        runTest {
            val scope = CoroutineScope(Job() + testScheduler)

            val deferred = scope.async { // root coroutine
                throw RuntimeException("my exception")
                42
            }

            try {
                deferred.await()
            } catch (ex: Exception) {
                println("Caught: $ex") // handled
            }
        }

    @Test
    fun `root coroutine - direct child of supervisorScope - normal exception handling behavior`() =
        runTest {
            supervisorScope {
                val deferred = async { // root coroutine
                    delay(100)
                    throw RuntimeException("my exception")
                    42
                }

                try {
                    deferred.await()
                } catch (ex: Exception) {
                    println("Caught: $ex") // handled
                }
            }
        }

    @Test
    fun `root coroutine - normal exception handling behavior - another example 1`() = runTest {
        launch {
            supervisorScope {
                try {
                    async { // a root coroutine
                        throw RuntimeException("my exception")
                        42
                    }.await()
                } catch (ex: Exception) {
                    println("Caught: $ex")  // handled
                }
            }
        }

    }

    @Test
    fun `root coroutine - normal exception handling behavior - another example 2`() = runTest {
        val scope = CoroutineScope(Job() + testScheduler)

        scope.launch {
            val deferred = scope.async { // root coroutine
                delay(100)
                throw RuntimeException("my exception")
                42
            }
            try {
                deferred.await()
            } catch (ex: Exception) {
                println("Caught: $ex")  // handled
            }
        }
    }

    /**
     * Non-root coroutine cases
     */

    @Test
    fun `non-root coroutine - uncaught exception propagates`() = runTest {

        val deferred = async { // Not a root coroutine
            delay(100)
            throw RuntimeException("my exception")
            42
        }

        try {
            deferred.await()
        } catch (ex: Exception) {
            println("Caught: $ex") // covered, but not handled
        }
    }

    @Test
    fun `non-root coroutine, coroutineScope - propagate exception`() = runTest {
        coroutineScope {
            val deferred = async { // non root coroutine
                throw RuntimeException("my exception")
                42
            }

            try {
                deferred.await()
            } catch (ex: Exception) {
                println("Caught: $ex") // covered, but not handled
            }
        }
    }

}