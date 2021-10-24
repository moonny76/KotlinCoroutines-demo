package com.org.scarlet.coroutines.exceptions

import com.org.scarlet.util.testDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.io.IOException
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CE04_ExceptionHandlerTest {

    val ehandler = CoroutineExceptionHandler { _, exception ->
        println("Global exception handler Caught $exception")
    }

    /**
     * Coroutine Exception Handler
     */

    @Test
    fun `CoroutineExceptionHandler at the scope`() = runBlockingTest {
        val scope = CoroutineScope(SupervisorJob() + testDispatcher + ehandler)

        val parent = scope.launch {
            launch {
                delay(500)
                throw RuntimeException("oops")
            }.invokeOnCompletion { println("child 1: exception = $it") }

            launch {
                delay(1000)
            }.invokeOnCompletion { println("child 2: exception = $it") }
        }.apply { invokeOnCompletion { println("parent: exception = $it") } }

        parent.join()
    }

    @Test
    fun `CoroutineExceptionHandler at the root coroutine - child of scope`() = runBlockingTest {

        val scope = CoroutineScope(SupervisorJob() + testDispatcher)
        val parent = scope.launch(ehandler) {
            launch {
                delay(100)
                throw RuntimeException("oops")
            }.invokeOnCompletion { println("child 1: exception = $it") }

            launch {
                delay(200)
            }.invokeOnCompletion { println("child 2: exception = $it") }
        }.apply { invokeOnCompletion { println("parent: exception = $it") } }

        parent.join()
    }

    @Test
    fun `CoroutineExceptionHandler at the root coroutine - child of supervisorScope`() =
        runBlockingTest {
            supervisorScope {
                launch(ehandler) {
                    launch {
                        delay(500)
                        throw RuntimeException("oops")
                    }.invokeOnCompletion { println("child 1: exception = $it") }

                    launch {
                        delay(1000)
                    }.invokeOnCompletion { println("child 2: exception = $it") }
                }.apply { invokeOnCompletion { println("parent: exception = $it") } }
            }
        }

    @Test
    fun `CoroutineExceptionHandler not at the root coroutine - child of coroutineScope`() =
        runBlockingTest {
            coroutineScope {
                val parent = launch(ehandler) {
                    launch {
                        delay(500)
                        throw RuntimeException("oops")
                    }.invokeOnCompletion { println("child 1: exception = $it") }

                    launch {
                        delay(1000)
                    }.invokeOnCompletion { println("child 2: exception = $it") }
                }.apply { invokeOnCompletion { println("parent: exception = $it") } }
            }
        }

    @Test
    fun `CoroutineExceptionHandler not at the root coroutine - not a direct child of scope`() =
        runBlockingTest {
            val scope = CoroutineScope(SupervisorJob() + testDispatcher)
            val parent = scope.launch {
                launch(ehandler) {
                    delay(500)
                    throw RuntimeException("oops")
                }.invokeOnCompletion { println("child 1: exception = $it") }

                launch {
                    delay(1000)
                }.invokeOnCompletion { println("child 2: exception = $it") }
            }.apply { invokeOnCompletion { println("parent: exception = $it") } }

            parent.join()
        }

    @Test
    fun `lecture note example - who's my parent`() = runBlockingTest {
        var child1: Job? = null
        var child2: Job? = null

        val parentJob = launch(SupervisorJob()) {
            child1 = launch {
                delay(500)
                throw IOException("failure")
            }.apply { invokeOnCompletion { println("child 1: exception = $it") } }
            child2 = launch {
                delay(1000)
            }.apply { invokeOnCompletion { println("child 2: exception = $it") } }

        }.apply { invokeOnCompletion { println("parentJob: exception = $it") } }

        parentJob.join()

        println("parentJob: isCanceled = ${parentJob.isCancelled}")
        println("child1: isCanceled = ${child1?.isCancelled}")
        println("child2: isCanceled = ${child2?.isCancelled}")
    }
}

