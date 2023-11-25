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

    private val ehandler = CoroutineExceptionHandler { context, exception ->
        log("Global exception handler: Caught $exception in $context")
    }

    @Test
    fun `try-catch inside async`() = runTest {
        onCompletion("runTest")

        val deferred = async {
            try {
                throw RuntimeException("my exception")
            } catch (ex: Exception) {
                log("Caught: $ex") // caught
            }
            42
        }.onCompletion("deferred")

        log("result = ${deferred.await()}")
    }

    /**
     * Root Coroutines Cases
     *
     * Jungsun's note:
     * Documentation says the exposed exception will be silently dropped unless
     * `.await()` is called on the deferred value.
     * However, actually it propagates and cancels all siblings and the scope
     * without failing the test.
     * Therefore, structured concurrency still works, but we have a chance to handle
     * exceptions using `try-catch`.
     */

    @Test
    fun `root coroutine - direct child of scope`() = runTest {
        val scope = CoroutineScope(SupervisorJob() + testDispatcher).onCompletion("scope")

        // a root coroutine
        val deferred: Deferred<Int> = scope.async {
            delay(1_000)
            throw RuntimeException("Oops!")
        }.onCompletion("deferred")

        // Comment out the entire try block and see whether exception still happens.
        try {
            deferred.await()
        } catch (ex: Exception) {
            log("Caught: $ex") // Caught and handled
        }
    }

    @Test
    fun `root coroutine - direct child of scope - what a surprise 😱`() =

        runTest {
            val scope = CoroutineScope(Job() + testDispatcher).onCompletion("scope")

            // root coroutine
            val parent: Deferred<Int> = scope.async {
                delay(1_000)
                throw RuntimeException("my exception")
            }.onCompletion("parent")

            scope.launch {
                delay(1500)
                log("sibling done")
            }.onCompletion("sibling")

            // Comment out the entire try block and see whether exception still happens.
            try {
                parent.await() // Normal exception will be thrown at this point
            } catch (ex: Exception) {
                log("Caught: $ex") // caught and handled
            }
        }

    @Test
    fun `root coroutine - direct child of supervisorScope - exposed exception`() =
        runTest {
            supervisorScope {
                onCompletion("supervisorScope")

                val deferred: Deferred<Int> = async { // root coroutine
                    delay(100)
                    throw RuntimeException("my exception")
                }.onCompletion("child")

                launch {
                    delay(1500)
                    log("sibling done")
                }.onCompletion("sibling")

                try {
                    deferred.await()
                } catch (ex: Exception) {
                    log("Caught: $ex") // caught and handled
                }
            }
        }

    /**
     * Quiz: Why `whoAmI` coroutine cancelled?
     */
    @Test
    fun `root coroutine - exposed exception - another example`() = runTest {
        val scope = CoroutineScope(Job() + testDispatcher)

        scope.launch {
            val deferred: Deferred<Int> = scope.async { // root coroutine
                delay(100)
                throw RuntimeException("Oops!")
            }.onCompletion("child")

            try {
                deferred.await()
            } catch (ex: Exception) {
                log("Caught: $ex")
            }
        }.onCompletion("whoAmI").join()

        scope.completeStatus("scope")
    }

    @Test
    fun `exception handler_of_no_use #1 - because not propagated exceptions`() = runTest {
        val scope = CoroutineScope(Job() + testDispatcher + ehandler).onCompletion("scope")

        val deferred = scope.async {
            delay(1_000)
            throw RuntimeException("Oops!")
        }.onCompletion("child")

        deferred.await() // Exception will be thrown at this point
    }

    @Test
    fun `exception handler of no use #2 - not propagated exceptions`() = runTest {
        supervisorScope {
            onCompletion("supervisorScope")

            val deferred: Deferred<Int> = async(ehandler) { // root coroutine
                delay(1000)
                throw RuntimeException("my exception")
            }.onCompletion("child")

            launch {
                delay(1500)
                log("sibling done")
            }.onCompletion("sibling")

            deferred.await() // Exception will be thrown at this point
        }
    }

    /**
     * Non-Root Coroutines Case
     */

    @Test
    fun `non-root coroutine - uncaught exception propagates`() = runTest {
        onCompletion("Top-level coroutine")

        // Not a root coroutine
        val deferred: Deferred<Int> = async {
            delay(1_000)
            throw RuntimeException("Oops!") // Exception will be thrown at this point, and propagate to parent
        }.onCompletion("deferred")

        // Unlike documentation says it is useless,
        // exceptions covered, but not considered as handled!!! <-- another surprise!😱
        try {
            deferred.await()
        } catch (ex: Exception) {
            log("Caught: $ex")
        }
    }

    @Test
    fun `non-root coroutine, coroutineScope - propagate exception`() = runTest {
        coroutineScope {
            // non root coroutine
            val deferred: Deferred<Int> = async {
                throw RuntimeException("Oops!")
            }.onCompletion("deferred")

            try {
                deferred.await()
            } catch (ex: Exception) {
                log("Caught: $ex") // Covered, but not considered as handled
            }
        }
    }


    @Test
    fun `exception handler of no use #3`() = runTest {
        // Not a root coroutine
        async(ehandler) {
            delay(1_000)
            throw RuntimeException("my exception")
        }.onCompletion("child").join()
    }

    @Test
    fun `exception handler of no use #4`() = runTest {

        // Not a root coroutine
        async(ehandler) {
            async {
                delay(1_000)
                throw RuntimeException("my exception")
            }.onCompletion("child")
        }.onCompletion("parent")

    }

}

