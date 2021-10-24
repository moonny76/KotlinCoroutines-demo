package com.org.scarlet.coroutines.exceptions

import com.org.scarlet.util.testDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CE03_Launch_ScopeBuilderTest {

    /**
     * supervisorScope has a SupervisorJob() and acts as a direct parent to root coroutine.
     * supervisorJob neither rethrow nor propagates uncaught exceptions!!
     */

    // runBlockingTest throws any uncaught exception!!
    @Test
    fun `supervisorScope test`() = runBlockingTest {
        try {
            supervisorScope {
                coroutineContext[Job]?.invokeOnCompletion { println("supervisorScope: exception = $it") }

                val child1 = launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { println("child1: exception = $it") } }

                val child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { println("child2: exception = $it") } }

                joinAll(child1, child2)

                println("child1: isCancelled = ${child1.isCancelled}")
                println("child2: isCancelled = ${child2.isCancelled}")
            }
        } catch (ex: Exception) { // useless
            println("Caught: $ex")
        }
    }

    /**
     * Beware that parentJob's parent Job is not a SupervisorJob!!
     */
    // runBlockingTest throws any uncaught exception!!
    @Test
    fun `supervisorScope test - another`() = runBlockingTest {
        supervisorScope {
            coroutineContext[Job]?.invokeOnCompletion { println("supervisorScope: exception = $it") }

            var child1: Job? = null
            var child2: Job? = null

            val parentJob = launch {
                child1 = launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { println("child1: $it") } }

                child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { println("child2: $it") } }
            }.apply { invokeOnCompletion { println("parentJob: $it") } }

            val siblingJob = launch {
                delay(1000)
            }.apply { invokeOnCompletion { println("siblingJob: $it") } }

            joinAll(parentJob, siblingJob)

            println("parentJob: isCancelled = ${parentJob.isCancelled}")
            println("child1: isCancelled = ${child1?.isCancelled}")
            println("child2: isCancelled = ${child2?.isCancelled}")
            println("siblingJob: isCancelled = ${siblingJob.isCancelled}")
        }
    }

    @Test
    fun `supervisorScope as a child of other coroutine`() = runBlockingTest {
        val scope = CoroutineScope(Job() + testDispatcher)

        val parentJob = scope.launch {
            supervisorScope {
                coroutineContext[Job]?.invokeOnCompletion { println("supervisorScope: exception = $it") }

                val child1 = launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { println("child1: exception = $it") } }

                val child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { println("child2: exception = $it") } }

                joinAll(child1, child2)

                println("supervisorScope: isCancelled = ${coroutineContext[Job]?.isCancelled}")
                println("child1: isCancelled = ${child1.isCancelled}")
                println("child2: isCancelled = ${child2.isCancelled}")
            }
        }.apply { invokeOnCompletion { println("parentJob: exception = $it") } }

        parentJob.join()
        println("parentJob: isCancelled = ${parentJob.isCancelled}")
    }

    /**
     * coroutineScope has a Job().
     * coroutineScope - a failing child does cancels the parent and its siblings.
     * Does not propagate exception, just rethrows it!!
     */
    @Test
    fun `coroutineScope rethrows exception`() = runBlockingTest {
        try {
            // rethrows uncaught exception
            coroutineScope {
                coroutineContext[Job]?.invokeOnCompletion { println("coroutineScope: exception = $it") }

                val child1 = launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { println("child1: exception = $it") } }

                val child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { println("child2: exception = $it") } }

                joinAll(child1, child2)

                // will be skipped
                println("coroutineScope: isCancelled = ${coroutineContext[Job]?.isCancelled}") // no effect
                println("child1: isCancelled = ${child1.isCancelled}")
                println("child2: isCancelled = ${child2.isCancelled}")
            }
        } catch (ex: Exception) {
            println("Caught: $ex")
        }
    }

    @Test
    fun `coroutineScope as a child of other coroutine`() = runBlockingTest {
        val scope = CoroutineScope(Job() + testDispatcher)

        val parentJob = scope.launch {
            coroutineScope {
                coroutineContext[Job]?.invokeOnCompletion { println("coroutineScope: exception = $it") }

                val child1 = launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { println("child1: exception = $it") } }

                val child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { println("child2: exception = $it") } }

                joinAll(child1, child2)

                // will be skipped
                println("child1: isCancelled = ${child1.isCancelled}")
                println("child2: isCancelled = ${child2.isCancelled}")
            }
        }.apply { invokeOnCompletion { println("parentJob: exception = $it") } }

        parentJob.join()
        println("parentJob: isCancelled = ${parentJob.isCancelled}")
        println("scope: isCancelled = ${scope.coroutineContext[Job]?.isCancelled}")
    }
}