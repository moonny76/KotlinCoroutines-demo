package com.scarlet.coroutines.advanced

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*

object Canceling_Parent_Cancels_All_Children {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        // What if change to Job()
        val scope = CoroutineScope(SupervisorJob())

        val child1 = scope.launch {
            log("child1 started")
            delay(1000)
            log("child1 done")
        }.onCompletion("child 1")

        val child2 = scope.launch {
            log("child2 started")
            delay(1000)
            log("child2 done")
        }.onCompletion("child 2")

        delay(500)

        scope.cancel()
        joinAll(child1, child2)

        log("is Parent scope cancelled? = ${TODO()}")
    }
}

object Canceling_A_Child_Cancels_Only_The_Target_Child_Including_All_Its_Descendants_If_Any {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        // What if change to Job()
        val scope = CoroutineScope(SupervisorJob())

        val child1 = scope.launch {
            log("child1 started")
            delay(1000)
            log("child1 done")
        }.onCompletion("child 1")

        val child2 = scope.launch {
            log("child2 started")
            delay(1000)
            log("child2 done")
        }.onCompletion("child 2")

        delay(500)

        child1.cancel()
        joinAll(child1, child2)

        log("is Parent cancelled? = ${scope.coroutineContext.job.isCancelled}")
    }
}

object SupervisorJob_Child_Failure_SimpleDemo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        // What if change to Job()
        val scope = CoroutineScope(SupervisorJob())

        val child1 = scope.launch {
            log("child1 started")
            delay(500)
            throw RuntimeException("child 1 failed")
        }.onCompletion("child 1")

        val child2 = scope.launch {
            log("child2 started")
            delay(1000)
            log("child2 done")
        }.onCompletion("child 2")

        joinAll(child1, child2)

        log("is Parent cancelled? = ${scope.coroutineContext.job.isCancelled}")
    }
}


/**/

object Standalone_SupervisorJob_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val supervisorJob = SupervisorJob()

        val child1 = launch(supervisorJob) {
            log("child1")
            delay(500)
            throw RuntimeException("Oops")
        }.onCompletion("child1")

        val child2 = launch(supervisorJob) {
            log("child2")
            delay(1000)
        }.onCompletion("child2")

        joinAll(child1, child2)
        log("Done")
    }
}

object Demo1 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val scope = CoroutineScope(Job())

        val job = scope.launch {

            val job1 = launch {
                log("child1")
                println("starting Coroutine 1")
            }.onCompletion("child1")

            supervisorScope {
                val job2 = launch {
                    log("child2")
                    delay(500)
                    throw RuntimeException("oops")
                }.onCompletion("child2")

                val job3 = launch {
                    log("child3")
                    delay(1000)
                    println("starting Coroutine 3")
                }.onCompletion("child3")
            }
        }.onCompletion("parent")

        job.join()
        log(scope.coroutineContext.job.isCancelled)
    }
}

// Root coroutine: exposed
// Non-root coroutine: propagates
object Demo2 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        try {
            coroutineScope {
//            supervisorScope {
                val deferred = async {
                    throw RuntimeException("Oops")
                }.onCompletion("Child")

                delay(100)

                log("......... here ..........")

//                try {
//                    deferred.await()
//                } catch (e: Exception) {
//                    // Exception thrown in async WILL NOT be caught here
//                    // but still will be rethrown by the coroutineScope
//                    log("exception caught ...")
//                }
            }
        } catch (ex: Exception) {
            log("Outermost catch block")
        }

    }
}