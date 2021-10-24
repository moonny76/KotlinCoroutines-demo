package com.org.scarlet.coroutines.exceptions

import com.org.scarlet.util.testDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CE05_AsyncEHTest {

    @Test
    fun `try-catch inside async`() = runBlockingTest {

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
        runBlockingTest {
            val scope = CoroutineScope(Job() + testDispatcher)

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
        runBlockingTest {

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
    fun `root coroutine - normal exception handling behavior - another example 1`() = runBlockingTest {

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
    fun `root coroutine - normal exception handling behavior - another example 2`() = runBlockingTest {
        val scope = CoroutineScope(Job() + testDispatcher)

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
        }.join()
    }

    /**
     * Non-root coroutine cases
     */

    // runBlocking - error
    // runBlockingTest - handles it as normal if async is direct child of runBlockingTest --> strange!!
    @Test
    fun `non-root coroutine - uncaught exception propagates`() = runBlocking<Unit> {

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
    fun `non-root coroutine, coroutineScope - propagate exception`() = runBlockingTest {

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