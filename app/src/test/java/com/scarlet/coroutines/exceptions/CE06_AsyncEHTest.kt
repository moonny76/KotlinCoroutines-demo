package com.scarlet.coroutines.exceptions

import com.scarlet.util.completeStatus
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import com.scarlet.util.testDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class AsyncEHTest {

    @Test
    fun `try-catch inside async`() = runTest {

        val deferred = async {
            try {
                throw RuntimeException("my exception")
                42
            } catch (ex: Exception) {
                log("Caught: $ex") // caught
            }
        }

        log(deferred.await())
    }


    /**
     * Non-root coroutines case
     */

    @Test(expected = RuntimeException::class)
    fun `non-root coroutine - uncaught exception propagates`() = runTest {

        val deferred = async { // Not a root coroutine
            delay(1000)
            throw RuntimeException("my exception")
            42
        }

        try {
            deferred.await()
        } catch (ex: Exception) {
            log("Caught: $ex") // caught, but the above exception still propagates
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
                log("Caught: $ex") // caught, but still propagates
            }
        }
    }

    /**
     * Root coroutines cases
     */

    @Test
    fun `root coroutine - direct child of scope - exposed exception`() =
        runTest {
            val scope = CoroutineScope(Job() + testDispatcher)

            val deferred = scope.async { // root coroutine
                delay(1000)
                throw RuntimeException("my exception")
                42
            }

//            delay(2000) // Check to see what exception will be thrown.

            try {
                deferred.await()
            } catch (ex: Exception) {
                log("Caught: $ex") // caught and handled
            }

            scope.coroutineContext.job.completeStatus("scope")

            scope.launch {
                delay(2000)
            }.onCompletion("child")

            scope.coroutineContext.job.completeStatus("scope")
        }

    @Test
    fun `root coroutine - direct child of supervisorScope - exposed exception`() =
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
                    log("Caught: $ex") // caught and handled
                }
            }
        }

    /**
     * Quiz: Why `parent` coroutine cancelled?
     */
    @Test
    fun `root coroutine - exposed exception - another example`() = runTest {
        val scope = CoroutineScope(Job() + testDispatcher)

        val parent = scope.launch {
            val deferred = scope.async { // root coroutine
                delay(100)
                throw RuntimeException("my exception")
                42
            }

            try {
                deferred.await()
            } catch (ex: Exception) {
                log("Caught: $ex")  // caught and handled? - comment out try-catch and see what happens
            }

        }.onCompletion("parent")

        parent.join()
        scope.coroutineContext.job.completeStatus("scope")
    }

}