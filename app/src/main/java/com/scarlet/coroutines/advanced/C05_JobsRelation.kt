package com.scarlet.coroutines.advanced

import com.scarlet.util.onCompletion
import kotlinx.coroutines.*

@DelicateCoroutinesApi
object Dependency_Between_Jobs {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        // coroutine starts when start() or join() called
        val job1 = launch(start = CoroutineStart.LAZY) {
            delay(100)
            println("Pong")
        }

        launch {
            println("Ping")
            job1.join()
            println("Ping")
        }
    }
}

object Jobs_Forms_Hierarchy {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parentJob = launch {
            println("I am parent")

            launch {
                println("\tI am a child the parentJob")
                delay(1000)
            }.invokeOnCompletion { println("\tchild completes") }

            launch { // To check whether already finished child counted as children
                println("\tI am another child of the parentJob")
                delay(500)
            }.invokeOnCompletion { println("\tanother child completes") }

        }.apply{
            invokeOnCompletion { println("parentJob completes") }
        }

        launch {
            println("I’m a sibling of the parentJob, not its child")
            delay(1000)
        }

        delay(300)
        println("The parentJob has ${parentJob.children.count()} children")

        delay(300)
        println("The parentJob has ${parentJob.children.count()} children")
    }
}

object In_Hierarchy_Parent_Waits_Until_All_Children_Finished {

    /**
     * Parental responsibilities:
     *
     * A parent coroutine always waits for completion of all its children.
     * A parent does not have to explicitly track all the children it launches,
     * and it does not have to use Job.join to wait for them at the end:
     */

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        // launch a coroutine to process some kind of incoming request
        val parent = launch {
            repeat(3) { i -> // launch a few children jobs
                launch { // try Dispatchers.Default
                    delay((i + 1) * 200L) // variable delay 200ms, 400ms, 600ms
                    println("\tChild Coroutine $i is done")
                }
            }
            println("parent: I'm done, but will wait until all my children completes")
            // No need to join here
        }.onCompletion("parent: now, I am completed")

        parent.join() // wait for completion of the request, including all its children
        println("Processing of the request is complete")
    }
}

object In_Hierarchy_Parent_Waits_Until_All_Children_Finished_Other_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parentJob = launch {
            println("I’m the parent")
        }.onCompletion("Finally, parent finished ...")

        launch(parentJob) {
            println("\tI’m a child")
            delay(1000)
        }.onCompletion("\tChild finished after 1000")

        println("The Parent job has ${parentJob.children.count()} child right after child launch")
        println("is Parent still alive right after child launch? ${parentJob.isActive}")

        delay(500)
        println("is Parent still alive at 500? ${parentJob.isActive}")

        parentJob.join()
        println("is Parent still alive after joined? ${parentJob.isActive}")
    }
}


