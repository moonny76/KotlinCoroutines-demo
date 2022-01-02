package com.scarlet.coroutines.cancellation

import com.scarlet.util.onCompletion
import kotlinx.coroutines.*

/**
 * The Job interface has a method cancel, that allows its cancellation.
 * Calling it triggers the following effects:
 * - Such a coroutine ends the job at the first suspension point (such as delay()).
 * - If a job has some children, they are canceled too (but its parent is not affected).
 * - Once a job is canceled, it cannot be used as a parent for any new coroutines,
 *   it is first in "Cancelling" and then in "Cancelled" state.
 */

object Cancel_Parent_Scope {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val scope = CoroutineScope(Job())

        var child: Job? = null
        val parent = scope.launch {
            child = launch {
                delay(1000)
            }.onCompletion("child")
        }.onCompletion("parent")

        delay(500)

        scope.cancel()
//        scope.coroutineContext[Job]?.cancelAndJoin()

        println("scope cancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent cancelled = ${parent.isCancelled}")
        println("child cancelled = ${child?.isCancelled}")
    }
}

object Cancel_Parent_Coroutine {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val scope = CoroutineScope(Job())

        var child1: Job? = null
        var child2: Job? = null
        val parentJob = scope.launch {
            child1 =
                launch { delay(1000) }.onCompletion("child1")
            child2 =
                launch { delay(1000) }.onCompletion("child2")
        }.onCompletion("parentJob")

        delay(500)

        parentJob.cancelAndJoin()

        println("scope cancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent job cancelled = ${parentJob.isCancelled}")
        println("child1 job cancelled = ${child1?.isCancelled}")
        println("child2 job cancelled = ${child2?.isCancelled}")
    }
}

object Cancel_Child_Coroutine {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val scope = CoroutineScope(Job())

        var child1: Job? = null
        var child2: Job? = null
        val parentJob = scope.launch {
            child1 =
                launch { delay(1000) }.onCompletion("child1")
            child2 =
                launch { delay(1000) }.onCompletion("child2")
        }.onCompletion("parentJob")

        delay(500)

        child1?.cancel()
        parentJob.join()

        println("scope cancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent job cancelled = ${parentJob.isCancelled}")
        println("child1 job cancelled = ${child1?.isCancelled}")
        println("child2 job cancelled = ${child2?.isCancelled}")
    }
}

/**
 * Quiz
 */
object Cancel_Parent_Job_Quiz {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val scope = CoroutineScope(Job())
        val job = Job()

        // Who's my parent?
        val child = scope.launch(job) {
            delay(1000)
        }.onCompletion("child")

        delay(500)

        // How to cancel the child via its parent?
        // job.cancel() or scope.cancel() ?

        child.join()

        println("scope cancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("child cancelled = ${child.isCancelled}")
    }
}

object Cancel_Children_Only_To_Reuse_Parent_Job {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val scope = CoroutineScope(Job())

        var child1: Job? = null
        var child2: Job? = null
        val parentJob = scope.launch {
            child1 = launch { delay(1000) }.onCompletion("child1")
            child2 = launch { delay(1000) }.onCompletion("child2")
        }.onCompletion("parentJob")

        delay(500)

        parentJob.cancelChildren()
        parentJob.join()

        println("scope cancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent job cancelled = ${parentJob.isCancelled}")
        println("child1 job cancelled = ${child1?.isCancelled}")
        println("child2 job cancelled = ${child2?.isCancelled}")
    }
}

object Cancel_Children_Only_To_Reuse_Scope {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val scope = CoroutineScope(Job())

        var child1: Job? = null
        var child2: Job? = null
        val parentJob = scope.launch {
            child1 = launch { delay(1000) }.onCompletion("child1")
            child2 = launch { delay(1000) }.onCompletion("child2")
        }.onCompletion("parentJob")

        delay(500)

        scope.coroutineContext[Job]?.cancelChildren()
        parentJob.join()

        println("scope cancelled = ${scope.coroutineContext[Job]?.isCancelled}")
        println("parent job cancelled = ${parentJob.isCancelled}")
        println("child1 job cancelled = ${child1?.isCancelled}")
        println("child2 job cancelled = ${child2?.isCancelled}")
    }
}



