package com.scarlet.coroutines.exceptions

import com.scarlet.util.completeStatus
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class SupervisorScopeBuilderTest {

    /**
     * `supervisorScope` has a `SupervisorJob()` and acts as a parent to root coroutines.
     *
     * (Wrong!!!!)
     * `supervisorScope` does not rethrow an uncaught exception, but propagates it instead!!
     *
     * Failure of a child coroutine does not propagate to its parent. But, failure of the scope
     * itself rethrows the exception unless it is caught.
     *
     * This feature requires an installed `CoroutineExceptionHandler` in its root coroutines,
     * otherwise the `supervisorScope` will fail anyway. That's because a scope always looks
     * for an installed exception handler. If it can't find any, it fails.
     *
     * Note: Those exceptions not propagated from child coroutines are not propagated, but
     * rethrown instead.
     */

    /**
     * Exceptions propagated via `supervisorScope` do not cancel the parent. <== Strange!!
     */

    /**
     * `runBlocking` vs. `runTest`
     *
     * `runBlocking` do not rethrow uncaught exception propagated via `supervisorScope`.
     *
     * But, `runTest` do rethrow it.
     */

    @Test
    fun `supervisorScope propagates propagated uncaught exceptions - runBlocking`() =
        /* runBlocking may call default CEH, which prints the stack trace.
           runBlocking regards this as the exception as handled. */
        runBlocking<Unit> {
            launch {
                supervisorScope {
                    launch {
                        delay(100)
                        throw RuntimeException("oops")
                    }.onCompletion("child1")
                }
                log("parent: Hey, I'm still alive!")
            }.onCompletion("parent")
        }

    @Test
    fun `supervisorScope propagates propagated uncaught exceptions - runTest`() =
        /* runTest calls installed CEH at TestScope, which prints the stack trace.
           and rethrows first uncaught exception as not handled */
        runTest {
            launch {
                supervisorScope {
                    launch {
                        delay(100)
                        throw RuntimeException("oops")
                    }.onCompletion("child1")
                }
                log("Hey, I'm still alive!")
            }.onCompletion("parent")
        }


    @Test
    fun `supervisorScope looks for CEH to handle exceptions from children`() =
        runTest {
            try {
                supervisorScope {
                    onCompletion("supervisorScope")

                    val child = launch {
                        delay(500)
                        throw RuntimeException("oops")
                    }.onCompletion("child")

                    child.join()

                    log("Am i alive? (supervisorScope)")
                }
                log("Am i alive? (top-level)")
            } catch (ex: Exception) {
                log("Caught: $ex") // useless
            }
        }

    @Test //(expected = RuntimeException::class)
    fun `supervisorScope - failed child doesn't affect its parent nor siblings`() = runTest {
        try {
            supervisorScope {
                onCompletion("supervisorScope")

                launch {
                    delay(100)
                    throw RuntimeException("oops")
                }.onCompletion("child1")

                launch {
                    delay(200)
                }.onCompletion("child2")
            }.onCompletion("parent")

        } catch (ex: Exception) {
            log("Caught: $ex") // useless
        }
    }

    @Test
    fun `supervisorScope rethrows its own exceptions including cancellation`() = runTest {
        try {
            // rethrows its own uncaught exception
            supervisorScope {
                onCompletion("supervisorScope")

                launch {
                    delay(500)
                }.onCompletion("child")

                delay(100)
                throw RuntimeException("Oops")
            }

        } catch (ex: Exception) {
            log("Caught: $ex")
        }
    }


    @Test
    fun `supervisorScope - cancelling the scope cancels itself and all its children`() = runTest {
        try {
            // rethrows its own uncaught exception
            supervisorScope {
                onCompletion("supervisorScope")

                launch { delay(500) }.onCompletion("child1")
                launch { delay(500) }.onCompletion("child2")

                delay(100)

                cancel()
//                coroutineContext.job.cancelChildren()
            }
        } catch (ex: Exception) {
            log("Caught: $ex")
        }
    }

    /**
     * Quiz: Who's my parent again?
     */

    @Test
    fun `supervisorScope - quiz1`() = runTest {
        val scope = CoroutineScope(Job())

        supervisorScope {
            onCompletion("supervisorScope")

            scope.launch {
                launch { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
                launch { delay(200) }.onCompletion("child2")
            }.onCompletion("parent job")
                .join()  // why do we need this?
        }

        scope.completeStatus()
    }

    @Test
    fun `supervisorScope - quiz2`() = runTest {
        val scope = CoroutineScope(Job())

        scope.launch {
            onCompletion("supervisorScope")

            supervisorScope {
                launch { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
                launch { delay(200) }.onCompletion("child2")
            }
        }.onCompletion("parent job").join()

        scope.completeStatus()
    }

    @Test
    fun foo() = runTest {
        val handler = CoroutineExceptionHandler { _, exception ->
            log("CoroutineExceptionHandler got $exception")
        }

        val scope = CoroutineScope(Job())
        scope.onCompletion("scope")

        scope.launch {
            log("parent started")

            supervisorScope {
                coroutineContext.job.onCompletion("supervisorScope")

                launch {
                    log("child 1 started")
                    delay(2_000)
                    throw RuntimeException("OOPS!")
                }.onCompletion("child1")

                launch {
                    log("child 2 started")
                    delay(5_000)
                }.onCompletion("child2")

                log("inside subScope... ")
            }

        }.onCompletion("parent").join()
    }

}