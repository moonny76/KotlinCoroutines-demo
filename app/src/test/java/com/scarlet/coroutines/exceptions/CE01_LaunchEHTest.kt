package com.scarlet.coroutines.exceptions

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import java.lang.RuntimeException

/**
 * **Top-level coroutines**: coroutines without parent coroutine.
 *
 * **Root coroutines**:
 * - Coroutines that are a direct child of a `CoroutineScope` instance or `supervisorScope`
 * - All child coroutines (coroutines created in the context of another Job) delegate
 *   handling of their exceptions to their parent coroutine, which also delegates to the
 *   parent, and so on until the root.
 *
 *  Root scope do not propagate exceptions. Do default behavior of printing to console.
 */

@ExperimentalCoroutinesApi
class LaunchEHTest {

    private fun failingFunction() {
        throw RuntimeException("oops")
    }

    @Test
    fun `exception thrown`() {
        failingFunction()
    }

    // `runBlocking` and `runTest` rethrows uncaught exception.
    @Test
    fun `exception with runBlocking or runTest`() = runTest {
        failingFunction()
    }

    // `runBlocking` rethrows only the first propagated uncaught exception
    @Test
    fun `multiple exceptions - runBlocking`() = runBlocking<Unit> {
        coroutineContext.job.invokeOnCompletion { cause ->
            println("job completed with $cause")
        }

        launch {
            delay(10)
            throw RuntimeException("yellow")
        }
        launch {
            delay(10)
            throw IOException("mellow")
        }
    }

    // `runTest` rethrows only the first propagated uncaught exception
    @Test
    fun `multiple exceptions - runTest`() = runTest {
        coroutineContext.job.invokeOnCompletion { cause ->
            println("job completed with $cause")
        }

        val job1 = launch {
            delay(10)
            throw RuntimeException("yellow")
        }
        val job2 = launch {
            delay(10)
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
                log("Caught $ex")
            }
        }
    }

    // rethrows propagated uncaught exception
    @Test
    fun `Non-root coroutine - cannot handle propagated exception on site using try-catch`() =
        runTest {
            try {
                launch {
                    failingFunction()
                }
            } catch (ex: Exception) {
                log("Caught $ex")  // useless
            }
        }

    @Test
    fun `Failure of child cancels the parent and its siblings`() = runTest {

        val scope = CoroutineScope(Job().onCompletion("scope"))

        val parentJob = scope.launch {
            launch {
                delay(100)
                throw RuntimeException("oops")
            }.onCompletion("child1")

            launch { delay(1000) }.onCompletion("child2")
        }.onCompletion("parentJob")

        parentJob.join()
    }

}

