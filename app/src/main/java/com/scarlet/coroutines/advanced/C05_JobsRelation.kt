package com.scarlet.coroutines.advanced

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*

@DelicateCoroutinesApi
object Dependency_Between_Jobs {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        // coroutine starts when start() or join() called
        val job = launch(start = CoroutineStart.LAZY) {
            log("See when I am printed ...")
            delay(100)
            log("Pong")
        }

        delay(500)

        launch {
            log("Ping")
            job.join()
            log("Ping")
        }
    }
}

object Jobs_Forms_Hierarchy {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parentJob = launch {
            log("I am parent")

            launch {
                log("\t\tI am a child of the parentJob")
                delay(1000)
            }.invokeOnCompletion { log("\t\tchild completes") }

            launch { // To check whether already finished child counted as children
                log("\t\tI am another child of the parentJob")
                delay(500)
            }.invokeOnCompletion { log("\t\tanother child completes") }

        }.apply{
            invokeOnCompletion { log("parentJob completes") }
        }

        launch {
            log("I’m a sibling of the parentJob, not its child")
            delay(1000)
        }.invokeOnCompletion { log("sibling completes") }

        delay(300)
        log("The parentJob has ${parentJob.children.count()} children")

        delay(500) // By this time, another child of the parentJob should have already been completed
        log("The parentJob has ${parentJob.children.count()} children")
    }
}

object In_Hierarchy_Parent_Waits_Until_All_Children_Finished {

    /**
     * Parental responsibilities:
     *
     * A parent coroutine always waits for completion of all its children.
     * A parent does not have to explicitly track all the children it launches,
     * and it does **not** have to use `Job.join` to wait for them at the end:
     */

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // launch a coroutine to process some kind of incoming request
        val parent = launch {
            repeat(3) { i -> // launch a few children jobs
                launch { // try Dispatchers.Default
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                    log("\t\tChild Coroutine $i is done")
                }
            }
            log("parent: I'm done, but will wait until all my children completes")
            // No need to join here
        }.onCompletion("parent: now, I am completed")

        parent.join() // wait for completion of the request, including all its children
        log("Done")
    }
}

/**
 * When a coroutine is launched in the `CoroutineScope` of another coroutine,
 * it inherits its context via `CoroutineScope.coroutineContext` and the `Job`
 * of the new coroutine becomes a child of the parent coroutine's job.
 *
 * When the parent coroutine is cancelled, all its children are recursively cancelled,
 * too. However, this parent-child relation can be explicitly overridden in one
 * of two ways:
 *
 * 1. When a _different scope is explicitly specified_ when launching a coroutine
 *    (for example, `GlobalScope.launch`), then it does not inherit a coroutine
 *    context from the original parent scope.
 * 2. **When a different `Job` object is passed as the context for the new coroutine,
 *    then it overrides the Job of the parent scope.**
 *
 * In both cases, the launched coroutine is not tied to the scope it was launched
 * from and operates independently.
 */

object In_Hierarchy_Parent_Waits_Until_All_Children_Finished_Other_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parentJob = launch {
            log("I’m the parent")
        }.onCompletion("Finally, parent finished ...")

        launch(parentJob) {
            log("\t\tI’m a child")
            delay(1000)
        }.onCompletion("\t\tChild finished after 1000")

        delay(100)

        log("The Parent job has ${parentJob.children.count()} children at around 100ms")
        log("is Parent active at around 100ms? ${parentJob.isActive}")

        delay(500)
        log("is Parent still active at around 600ms? ${parentJob.isActive}")

        parentJob.join()
        log("is Parent still active after joined? ${parentJob.isActive}")
    }
}


