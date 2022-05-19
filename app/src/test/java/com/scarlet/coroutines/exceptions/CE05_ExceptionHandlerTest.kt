package com.scarlet.coroutines.exceptions

import com.scarlet.util.completeStatus
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import com.scarlet.util.testDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class ExceptionHandlerTest {

    private val ehandler = CoroutineExceptionHandler { _, exception ->
        log("Global exception handler Caught $exception")
    }

    /**
     * Coroutine Exception Handler
     */

    @Test
    fun `CEH at the scope`() = runTest {
        val scope = CoroutineScope(Job() + testDispatcher + ehandler)

        val parent = scope.launch {
            launch {
                log("Child 1 started")
                delay(10_000)
                throw RuntimeException("oops")
            }.onCompletion("child1")

            launch {
                log("Child 2 started")
                delay(20_000)
            }.onCompletion("child2")
        }.onCompletion("parent")

        parent.join()
        scope.coroutineContext.job.completeStatus("scope")
    }

    @Test
    fun `CEH at the root coroutine - child of scope`() = runTest {
        val scope = CoroutineScope(SupervisorJob() + testDispatcher)

        val parent = scope.launch(ehandler) {
            launch { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
            launch { delay(200) }.onCompletion("child2")
        }.onCompletion("parent")

        parent.join()
        scope.coroutineContext.job.completeStatus("scope")
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

    @Test(expected = RuntimeException::class)
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
        val scope = CoroutineScope(Job() + testDispatcher)

        scope.launch {
            launch(ehandler) { delay(100); throw RuntimeException("oops") }.onCompletion("child1")
            launch { delay(200) }.onCompletion("child2")
        }.onCompletion("parent")

    }
}

