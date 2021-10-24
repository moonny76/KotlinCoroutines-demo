package com.org.scarlet.coroutines.exceptions

import com.org.scarlet.util.testDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.uncaughtExceptions
import org.junit.Test
import java.io.IOException
import java.lang.RuntimeException

/**
 * Tol-level coroutines: coroutines without parent coroutine.
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

    // `runblockingTest` also rethrows uncaught exception.
    @Test
    fun `exception with runBlockingTest`() = runBlockingTest {
        failingFunction()
    }

    @Test
    fun `can handle rethrown exception on site using try-catch`() = runBlockingTest {
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
    fun `cannot handle propagated exception on site using try-catch - runBlocking`() = runBlocking<Unit>{
        try {
            launch {
                failingFunction()
            }
        } catch (ex: Exception) {
            println("Caught $ex")  // useless
        }
    }

    // rethrows propagated uncaught exception
    @Test
    fun `cannot handle propagated exception on site using try-catch - runBlockingTest`() = runBlockingTest {
        try {
            launch {
                failingFunction()
            }
        } catch (ex: Exception) {
            println("Caught $ex")  // useless
        }
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

    // Records and prints all propagated exceptions, but rethrows only the first exception.
    @Test
    fun `multiple exceptions - runBlockingTest`() = runBlockingTest {
        val job1 = launch {
            delay(10)
            throw RuntimeException("yellow")
        }
        val job2 = launch {
            delay(20)
            throw IOException("mellow")
        }

        joinAll(job1, job2)
        println(this.uncaughtExceptions)
    }

    @Test
    fun `Failure of child cancels the parent and its siblings`() = runBlockingTest {

        val scope = CoroutineScope(Job() + testDispatcher)
        scope.coroutineContext[Job]?.invokeOnCompletion { println("scope: exception = $it") }

        var child1: Job? = null
        var child2: Job? = null
        val parentJob = scope.launch {
            child1 = launch {
                delay(500)
                throw RuntimeException("oops")
            }.apply { invokeOnCompletion { println("child1: exception = $it") } }
            child2 = launch {
                delay(1000)
            }.apply { invokeOnCompletion { println("child2: exception = $it") } }
        }.apply { invokeOnCompletion { println("parentJob: exception = $it") } }

        parentJob.join()

        println("scope cancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent job cancelled = ${parentJob.isCancelled}")
        println("child1 job cancelled = ${child1?.isCancelled}")
        println("child2 job cancelled = ${child2?.isCancelled}")
    }

    // parent scope looks for CEH. If not exists, delegate to UncaughtCoroutineExceptionHandler
    // In Android, app crashes. In JVM, print to console.
    @Test
    fun `supervisorScope with runBlocking`() = runBlocking {
        try {
            supervisorScope {
                val child1 = launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { ex -> println("child1: $ex") } }

                val child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { ex -> println("child2: $ex") } }

                joinAll(child1, child2)
                println("is parent scope cancelled = ${coroutineContext[Job]?.isCancelled}")
            }
        } catch (ex: Exception) {
            println("Caught: $ex") // no need
        }

        println("Done")
    }

    // runBlockingTest rethrows unhandled exception!!
    @Test
    fun `supervisorScope with runBlockingTest`() = runBlockingTest {
        try {
            supervisorScope {
                val child1 = launch {
                    delay(200)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { ex -> println("child1: $ex") } }

                val child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { ex -> println("child2: $ex") } }

                joinAll(child1, child2)
                println("is parent scope cancelled = ${coroutineContext[Job]?.isCancelled}")
            }
        } catch (ex: Exception) {
            println("Caught: $ex") // no need
        }
        println("Done")
    }

    @Test
    fun `coroutineScope with runBlocking`() = runBlocking {
        try {
            coroutineScope {
                val child1 = launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { ex -> println("child1: $ex") } }

                val child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { ex -> println("child2: $ex") } }

                joinAll(child1, child2)
                println("is parent scope cancelled = ${coroutineContext[Job]?.isCancelled}")
            }
        } catch (ex: Exception) {
            println("Caught: $ex") // handled
        }
    }

    @Test
    fun `coroutineScope with runBlockingTest`() = runBlockingTest {
        try {
            coroutineScope { // exception rethrown
                val child1 = launch {
                    delay(500)
                    throw RuntimeException("oops")
                }.apply { invokeOnCompletion { ex -> println("child1: $ex") } }

                val child2 = launch {
                    delay(1000)
                }.apply { invokeOnCompletion { ex -> println("child2: $ex") } }

                joinAll(child1, child2)
                println("is parent scope cancelled = ${coroutineContext[Job]?.isCancelled}")
            }
        } catch (ex: Exception) {
            println("Caught: $ex") // handled
        }
    }

}

