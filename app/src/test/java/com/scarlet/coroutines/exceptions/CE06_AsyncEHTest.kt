package com.scarlet.coroutines.exceptions

import com.scarlet.util.completeStatus
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class AsyncEHTest {

    @Test
    fun `try-catch inside async`() = runTest {
        coroutineContext.job.onCompletion("runTest")

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
     * Non-root coroutines case
     */

    @Test
    fun `non-root coroutine - uncaught exception propagates`() = runBlocking<Unit> {

        // Not a root coroutine
        val deferred: Deferred<Int> = async {
            delay(1_000)
            throw RuntimeException("my exception") // Exception will be thrown at this point, and propagate to parent
        }.onCompletion("deferred")

        // Comment out the entire try block and see whether exception still happens.
        try {
            deferred.await()
        } catch (ex: Exception) {
            log("top-level coroutine: isCancelled = ${coroutineContext.job.isCancelled}")
            log("Caught: $ex") // Covered, but not considered as handled ???
        }
    }

    @Test(expected = RuntimeException::class)
    fun `non-root coroutine, coroutineScope - propagate exception`() = runTest {
        coroutineScope {
            // non root coroutine
            val deferred: Deferred<Int> = async {
                throw RuntimeException("my exception")
            }.onCompletion("deferred")

            try {
                deferred.await()
            } catch (ex: Exception) {
                log("Caught: $ex") // Covered, but not considered as handled ???
            }
        }
    }

    private val ehandler = CoroutineExceptionHandler { _, exception ->
        log("Global exception handler Caught $exception")
    }

    @Test(expected = RuntimeException::class)
    fun `exception handler of no use #1 - neither root coroutine nor propagated exception`() =
        runTest {
            // Not a root coroutine
            async(ehandler) {
                delay(1_000)
                throw RuntimeException("my exception") // Exception will be thrown at this point, and propagate to parent
            }.onCompletion("child").join()
        }

    @Test(expected = RuntimeException::class)
    fun `exception handler of no use #2`() = runTest {

        // Not a root coroutine
        async(ehandler) {
            async {
                delay(1_000)
                throw RuntimeException("my exception") // Exception will be thrown at this point, and propagate to parent
            }.onCompletion("child")
        }.onCompletion("parent")

    }

    /**
     * Root coroutines cases
     */

    @Test
    fun `root coroutine - direct child of scope`() = runTest {
        val scope = CoroutineScope(Job()).onCompletion("scope")

        // a root coroutine
        val deferred: Deferred<Int> = scope.async {
            delay(1_000)
            throw RuntimeException("my exception") // Exception will be thrown at this point, and propagate to parent
        }.onCompletion("deferred")

//        deferred.join()

        // Comment out the entire try block and see whether exception still happens.
        try {
            deferred.await()
        } catch (ex: Exception) {
            log("top-level coroutine: isCancelled = ${coroutineContext.job.isCancelled}")
            log("Caught: $ex")
        }
    }

    @Test
    fun `root coroutine - direct child of scope - exposed exception - what a surprise`() =
        /**
         * Documentation says the exception will be silently dropped.
         * However, actually it propagates and cancels all siblings and the scope
         * without failing the test.
         */
        runTest {
            val scope = CoroutineScope(Job()).onCompletion("scope")

            // root coroutine
            val parent: Deferred<Int> = scope.async {
                delay(1_000)
                throw RuntimeException("my exception")
            }.onCompletion("parent")

            // Uncomment block below and see what happens to sibling coroutines.
            scope.launch {
                delay(1500)
                log("sibling done")
            }.onCompletion("sibling")

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

                // Uncomment block below and see what happens to sibling coroutine.
//                launch {
//                    delay(1500)
//                    log("sibling done")
//                }.onCompletion("sibling")

                try {
                    deferred.await() // Exception will be thrown at this point
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
        val scope = CoroutineScope(Job())

        scope.launch {
            val deferred: Deferred<Int> = scope.async { // root coroutine
                delay(100)
                throw RuntimeException("my exception")
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
    fun `exception handler_of_no_use #3 - not propagated exception`() = runTest {
        val scope = CoroutineScope(Job() + ehandler).onCompletion("scope")

        val deferred = scope.async {
            delay(1_000)
            throw RuntimeException("my exception") // Exception will be thrown at this point, and propagate to parent
        }.onCompletion("child")

//        try {
        deferred.await()
//        } catch (ex: Exception) {
//            log("Caught: $ex")
//        }

        scope.completeStatus("scope")
    }

    @Test
    fun `exception handler of no use #4 - not propagated exception`() = runTest {
        supervisorScope {
            onCompletion("supervisorScope")

            val deferred: Deferred<Int> = async(ehandler) { // root coroutine
                delay(100)
                throw RuntimeException("my exception")
            }.onCompletion("child")

            // Uncomment block below and see what happens to sibling coroutine.
            launch {
                delay(1500)
                log("sibling done")
            }.onCompletion("sibling")

            try {
                deferred.await() // Exception will be thrown at this point
            } catch (ex: Exception) {
                log("Caught: $ex") // caught and handled
            }
        }
    }

}

