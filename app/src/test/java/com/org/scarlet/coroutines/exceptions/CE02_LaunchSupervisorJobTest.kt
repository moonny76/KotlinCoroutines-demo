package com.org.scarlet.coroutines.exceptions

import com.org.scarlet.util.testDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CE02_LaunchSupervisorJobTest {

    /**
     * SupervisorJob - failing child does not affect the parent and its sibling.
     * Warning: SupervisorJob only works when it is part of a coroutine's parent context!! <-- by kim
     */

    @Test
    fun `SupervisorJob in scope`() = runBlockingTest {
        val scope = CoroutineScope(SupervisorJob() + testDispatcher)

        val child1 = scope.launch {
            delay(500)
            throw RuntimeException("oops")
        }.apply { invokeOnCompletion { println("child1: exception = $it") } }

        val child2 = scope.launch {
            delay(1000)
        }.apply { invokeOnCompletion { println("child2: exception = $it") } }

        joinAll(child1, child2)
        println("scope: isCancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("child1: isCancelled = ${child1.isCancelled}")
        println("child2: isCancelled = ${child2.isCancelled}")
    }

    @Test
    fun `SupervisorJob in parent job`() = runBlockingTest {
        val scope = CoroutineScope(Job() + testDispatcher)
        val sharedJob = SupervisorJob()

        val child1 = scope.launch(sharedJob) {
            delay(500)
            throw RuntimeException("oops")
        }.apply { invokeOnCompletion { println("child1: exception = $it") } }

        val child2 = scope.launch(sharedJob) {
            delay(1000)
        }.apply { invokeOnCompletion { println("child2: exception = $it") } }

        joinAll(child1, child2)
        println("scope: isCancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent job: isCancelled = ${sharedJob.isCancelled}")
        println("child1: isCancelled = ${child1.isCancelled}")
        println("child2: isCancelled = ${child2.isCancelled}")
    }

    @Test
    fun `SupervisorJob in parent job - another demo`() = runBlockingTest {
        val scope = CoroutineScope(Job() + testDispatcher)
        val sharedJob = SupervisorJob()

        val child1 = scope.launch(sharedJob) {
            delay(500)
            throw RuntimeException("oops")
        }.apply { invokeOnCompletion { println("child1: exception = $it") } }

        val child2 = scope.launch(sharedJob) {
            delay(1000)
        }.apply { invokeOnCompletion { println("child2: exception = $it") } }

        val child3 = scope.launch {
            delay(1000)
        }.apply { invokeOnCompletion { println("child3: exception = $it") } }

        val child4 = scope.launch {
            delay(1000)
        }.apply { invokeOnCompletion { println("child4: exception = $it") } }

        joinAll(child1, child2, child3, child4)
        println("scope: isCancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent job: isCancelled = ${sharedJob.isCancelled}")
        println("child1: isCancelled = ${child1.isCancelled}")
        println("child2: isCancelled = ${child2.isCancelled}")
        println("child3: isCancelled = ${child3.isCancelled}")
        println("child4: isCancelled = ${child4.isCancelled}")
    }

    @Test
    fun `SupervisorJob not in parent context`() = runBlockingTest {
        val scope = CoroutineScope(Job() + testDispatcher)

        var child1: Job? = null
        var child2: Job? = null
        val parentJob = scope.launch(SupervisorJob()) { // SupervisorJob catches propagated exception, so scope not cancelled
            child1 = launch {
                delay(500)
                throw RuntimeException("oops")
            }.apply { invokeOnCompletion { println("child1: exception = $it") } }

            child2 = launch {
                delay(1000)
            }.apply { invokeOnCompletion { println("child2: exception = $it") } }

        }.apply { invokeOnCompletion { println("parentJob: execption = $it") } }

        parentJob.join()

        println("scope: isCancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent job: isCancelled = ${parentJob.isCancelled}")
        println("child1: isCancelled = ${child1?.isCancelled}")
        println("child2: isCancelled = ${child2?.isCancelled}")
    }
}
