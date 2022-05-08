package com.scarlet.coroutines.exceptions

import com.scarlet.util.completeStatus
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
class CE02_LaunchSupervisorJobTest {

    /**
     * SupervisorJob - failing child does not affect the parent and its sibling.
     *
     * Location of SupervisorJob matters:
     *  - Warning: SupervisorJob only works when it is part of a coroutine's parent context!! <-- by kim
     */

    /**
     * Quiz: Who's my parent?
     */

    @Test
    fun `lecture note example - who's my parent`() = runTest {

        val parentJob = launch(SupervisorJob()) {

            val child1 = launch {
                delay(100)
                throw IOException("failure")
            }.onCompletion("child1")

            val child2 = launch {
                delay(200)
            }.onCompletion("child2")

        }.onCompletion("parent")

        parentJob.join()
    }

    @Test
    fun `SupervisorJob in scope takes effect`() = runTest {
        val scope = CoroutineScope(SupervisorJob())

        val child1 = scope.launch {
            delay(100)
            throw RuntimeException("oops")
        }.onCompletion("child1")

        val child2 = scope.launch {
            delay(200)
        }.onCompletion("child2")

        joinAll(child1, child2)
        scope.completeStatus()
    }

    @Test
    fun `SupervisorJob in parent job controls lifetime of children`() = runTest {
        val scope = CoroutineScope(Job())
        val sharedJob = SupervisorJob()

        val child1 = scope.launch(sharedJob) {
            delay(100)
            throw RuntimeException("oops")
        }.onCompletion("child1")

        val child2 = scope.launch(sharedJob) {
            delay(200)
        }.onCompletion("child2")

        joinAll(child1, child2)
        sharedJob.completeStatus("sharedJob")
        scope.completeStatus()
    }

    @Test
    fun `SupervisorJob in parent job controls only the lifetime of its own children`() = runTest {
        val scope = CoroutineScope(Job())
        val sharedJob = SupervisorJob()

        val child1 = scope.launch(sharedJob) {
            delay(100)
            throw RuntimeException("oops")
        }.onCompletion("child1")

        val child2 = scope.launch(sharedJob) {
            delay(200)
        }.onCompletion("child2")

        val child3 = scope.launch {
            delay(200)
        }.onCompletion("child3")

        val child4 = scope.launch {
            delay(200)
        }.onCompletion("child4")

        joinAll(child1, child2, child3, child4)
        sharedJob.completeStatus("sharedJob")
        scope.completeStatus()
    }

    @Test
    fun `SupervisorJob not in parent context has no effect`() = runTest {
        val scope = CoroutineScope(Job())

        val parentJob = scope.launch(SupervisorJob()) {
            launch {
                throw RuntimeException("oops")
            }.onCompletion("child1")

            launch {
                delay(100)
            }.onCompletion("child2")

        }.onCompletion("parentJob")

        parentJob.join()

        scope.completeStatus()
    }

    @Test
    fun `SupervisorJob does not work when it is not part of a scope`() = runTest {
        try {
            val scope = CoroutineScope(Job())
            try {
                val parentJob = scope.launch(SupervisorJob()) {
                    launch {
                        delay(100)
                        throw RuntimeException("oops")
                    }.onCompletion("child1")
                    launch {
                        delay(1000)
                    }.onCompletion("child2")
                }.onCompletion("parentJob")
                parentJob.join()
            } catch (ex: Exception) {
                log("Exception caught: $ex") // No use
            }
            scope.completeStatus()
        } catch (ex: Exception) {
            log("Outer: Exception caught: $ex") // No use
        }
    }

}
