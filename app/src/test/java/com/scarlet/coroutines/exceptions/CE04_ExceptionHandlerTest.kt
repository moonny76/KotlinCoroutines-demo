package com.scarlet.coroutines.exceptions

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CE04_ExceptionHandlerTest {

    private val ehandler = CoroutineExceptionHandler { _, exception ->
        log("Global exception handler Caught $exception")
    }

    /**
     * Coroutine Exception Handler
     */

    @Test
    fun `CEH at the scope`() = runTest {
        val scope = CoroutineScope(SupervisorJob() /*+ testDispatcher*/ + ehandler)

        val parent = scope.launch {
            launch {
                delay(100)
                throw RuntimeException("oops")
            }.onCompletion("child1")

            launch {
                delay(200)
            }.onCompletion("child2")
        }.onCompletion("parent")

        parent.join()
    }

    @Test
    fun `CEH at the root coroutine - child of scope`() = runTest {
        val scope = CoroutineScope(SupervisorJob() + testScheduler)

        scope.launch(ehandler) {
            launch { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
            launch { delay(200) }.onCompletion("child2")
        }.onCompletion("parent")
    }

    @Test
    fun `CEH at the root coroutine - child of supervisorScope`() = runTest {
        supervisorScope {
            launch(ehandler) {
                launch { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
                launch { delay(200) }.onCompletion("child2")
            }.onCompletion("parent")
        }
    }

    @Test
    fun `CEH not at the root coroutine - child of coroutineScope`() = runTest {
        coroutineScope {
            launch(ehandler) {
                launch { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
                launch { delay(200) }.onCompletion("child2")
            }.onCompletion("parent")
        }
    }

    @Test
    fun `CEH not at the root coroutine - not a direct child of scope`() = runTest {
        val scope = CoroutineScope(SupervisorJob() + testScheduler)

        scope.launch {
            launch(ehandler) { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
            launch { delay(200) }.onCompletion("child2")
        }.onCompletion("parent")

    }
}

