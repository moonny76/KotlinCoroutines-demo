package com.scarlet.coroutines.exceptions

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import java.lang.RuntimeException

/**
 * Top-level coroutines: coroutines without parent coroutine.
 *
 * Root coroutines:
 * - Coroutines that are a direct child of a CoroutineScope instance or supervisorScope
 * - All children coroutines (coroutines created in the context of another Job) delegate
 *   handling of their exceptions to their parent coroutine, which also delegates to the
 *   parent, and so on until the root.
 *
 *  Root scope do not propagate exceptions. Do default behavior of printing to console.
 */

@ExperimentalCoroutinesApi
class CE01_LaunchEHTest {

    private fun failingFunction() {
        throw RuntimeException("oops")
    }

    @Test
    fun `exception thrown`() {
        failingFunction()
    }

    // `runblocking` rethrows uncaught exception.
    @Test
    fun `exception with runBlocking`() = runBlocking {
        failingFunction()
    }

    // rethrows only the first propagated uncaught exception
    @Test
    fun `multiple exceptions - runBlocking`() = runBlocking<Unit> {
        launch {
            delay(10)
            throw RuntimeException("yellow")
        }
        launch {
            delay(20)
            throw IOException("mellow")
        }
    }

    // rethrows only the first propagated uncaught exception
    @Test
    fun `multiple exceptions - runBlockingTest`() = runTest {
        val job1 = launch {
            delay(10)
            throw RuntimeException("yellow")
        }
        val job2 = launch {
            delay(20)
            throw IOException("mellow")
        }

        joinAll(job1, job2)
    }

    @Test
    fun `can handle rethrown exception on site using try-catch`() = runTest {
        launch {
            try {
                failingFunction()
            } catch (ex: Exception) {
                println("Caught $ex")
            }
        }
    }

    // rethrows propagated uncaught exception
    @Test
    fun `Non-root coroutine - cannot handle propagated exception on site using try-catch`() = runTest{
        try {
            launch {
                failingFunction()
            }
        } catch (ex: Exception) {
            println("Caught $ex")  // useless
        }
    }

    @Test
    fun `Failure of child cancels the parent and its siblings`() = runTest {

        val scope = CoroutineScope(Job() + testScheduler)
        scope.coroutineContext[Job]?.onCompletion("scope")

        var child1: Job? = null
        var child2: Job? = null
        val parentJob = scope.launch {

            child1 = launch {
                delay(100)
                throw RuntimeException("oops")
            }.onCompletion("child1")

            child2 = launch {
                delay(1000)
            }.onCompletion("child2")

        }.onCompletion("parentJob")

        parentJob.join()

        log("scope cancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        log("parent job cancelled = ${parentJob.isCancelled}")
        log("child1 job cancelled = ${child1?.isCancelled}")
        log("child2 job cancelled = ${child2?.isCancelled}")
    }

}

