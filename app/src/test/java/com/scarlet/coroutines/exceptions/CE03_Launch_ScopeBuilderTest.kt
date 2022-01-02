package com.scarlet.coroutines.exceptions

import com.scarlet.util.completeStatus
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CE03_Launch_ScopeBuilderTest {

    /**
     * coroutineScope has a Job().
     * A failing child causes the cancellation of its parent and siblings.
     * Does not propagate exception, just rethrows it!!
     */

    @Test
    fun `coroutineScope rethrows exception`() = runTest {
        try {
            // rethrows uncaught exception
            coroutineScope {
                coroutineContext.job.onCompletion("coroutineScope")

                launch {
                    delay(100)
                    throw RuntimeException("oops")
                }.onCompletion("child")
            }
        } catch (ex: Exception) {
            println("Caught: $ex")
        }
    }

    @Test
    fun `coroutineScope - cancelling the scope cancels itself and all its children`() = runTest {
        try {
            // rethrows uncaught exception
            coroutineScope {
                coroutineContext.job.onCompletion("coroutineScope")

                launch { delay(500) }.onCompletion("child1")
                launch { delay(500) }.onCompletion("child2")

                delay(100)

                coroutineContext.cancel()
//                coroutineContext.cancelChildren()
            }
        } catch (ex: Exception) {
            println("Caught: $ex")
        }
    }

    @Test
    fun `coroutineScope - failing child causes cancellation of its parent and sibling`() = runTest {
        try {
            // rethrows uncaught exception
            coroutineScope {
                coroutineContext.job.onCompletion("coroutineScope")

                launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.onCompletion("child1")

                launch { delay(1000) }.onCompletion("child2")
            }
        } catch (ex: Exception) {
            println("Caught: $ex")
        }
    }

    @Test
    fun `coroutineScope as a sub-scope of other coroutine`() = runTest {
        val scope = CoroutineScope(Job() + testScheduler)

        val parentJob = scope.launch {
            coroutineScope {
                coroutineContext.job.onCompletion("coroutineScope")

                launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.onCompletion("child1")

                launch { delay(1000) }.onCompletion("child2")
            }
        }.onCompletion("parentJob")

        parentJob.join()
    }


    /**
     * supervisorScope has a `SupervisorJob()` and acts as a direct parent to root coroutine.
     * `supervisorJob` do not rethrow uncaught exception, but propagates it instead!!
     */

    @Test
    fun `supervisorScope propagates exceptions from children`() = runTest {
        try {
            supervisorScope {
                coroutineContext.job.onCompletion("supervisorScope")

                launch {
                    delay(100)
                    throw RuntimeException("oops")
                }.onCompletion("child")
            }
        } catch (ex: Exception) {
            println("Caught: $ex") // useless
        }
    }

    @Test
    fun `supervisorScope rethrows its own exceptions including cancellation`() = runTest {
        try {
            supervisorScope {
                coroutineContext.job.onCompletion("supervisorScope")

                launch {
                    delay(500)
                }.onCompletion("child")

                delay(100)

                throw RuntimeException("Oops")
//                coroutineContext.cancel()
            }
        } catch (ex: Exception) {
            println("Caught: $ex")
        }
    }

    @Test
    fun `supervisorScope - failed child doesn't affect its parent nor siblings`() = runTest {
        try {
            supervisorScope {
                coroutineContext.job.onCompletion("supervisorScope")

                launch {
                    delay(100)
                    throw RuntimeException("oops")
                }.onCompletion("child1")

                launch { delay(200) }.onCompletion("child2")
            }
        } catch (ex: Exception) {
            println("Caught: $ex") // useless
        }
    }

    @Test
    fun `supervisorScope - cancelling the scope cancels itself and all its children`() = runTest {
        try {
            // rethrows uncaught exception
            supervisorScope {
                coroutineContext.job.onCompletion("supervisorScope")

                launch { delay(500) }.onCompletion("child1")
                launch { delay(500) }.onCompletion("child2")

                delay(100)

                coroutineContext.cancel() // cancelChildren()
            }
        } catch (ex: Exception) {
            println("Caught: $ex")
        }
    }

    /**
     * Quiz: Who's my parent?
     */

    @Test
    fun `supervisorScope - quiz1`() = runTest {
        val scope = CoroutineScope(Job() + testScheduler)

        supervisorScope {
            onCompletion("supervisorScope")

            scope.launch {
                launch { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
                launch { delay(200) }.onCompletion("child2")
            }.onCompletion("parent job")
        }

        scope.completeStatus()
    }

    @Test
    fun `supervisorScope - quiz2`() = runTest {
        val scope = CoroutineScope(Job() + testScheduler)

        val parentJob = scope.launch {
            supervisorScope {
                onCompletion("supervisorScope")

                launch { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
                launch { delay(200) }.onCompletion("child2")
            }
        }.onCompletion("parent job")

        parentJob.join()

        scope.completeStatus()
    }

}