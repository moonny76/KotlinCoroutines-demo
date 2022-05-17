package com.scarlet.coroutines.basics

import com.scarlet.model.User
import com.scarlet.util.log
import kotlinx.coroutines.*

private suspend fun save(user: User) {
    // simulate network delay
    delay(1000)
    log("User saved: $user")
}

object Launch_Demo1 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("1. before launch")

        launch {
            log("3. before save")
            save(User("A001", "Jody", 33))
            log("4. after save")
        }

        log("2. after launch")
    }

}

object Launch_Demo2 {

    @JvmStatic
    fun main(args: Array<String>) {
        log("0. Start")

        runBlocking {
            launch {
                delay(1000)
                log("2. child 1 done.")
            }
            launch {
                delay(2000)
                log("3. child 2 done.")
            }

            log("1. end of runBlocking")
        }

        log("4. Done")
    }
}

object Launch_Join_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("1. start of runBlocking")

        launch {
            log("2. child 1 start")
            delay(1000)
            log("3. child 1 done")
        }

        // How to print next line at the last?
        log("4. Done")
    }
}

@DelicateCoroutinesApi
object GlobalScope_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("1. start of runBlocking")

        GlobalScope.launch {
            log("2. before save")
            save(User("A001", "Jody", 33))
            log("3. after save")
        }.join()

        log("4. Done.")
    }

}

/**
 * See what happens when you use w/ or w/o `runBlocking`, and then when use `join`.
 */
object CoroutineScope_Sneak_Preview_Demo {

    @JvmStatic
    fun main(args: Array<String>) {
        val scope = CoroutineScope(Job())

        val job = scope.launch {
            log("before save")
            save(User("A001", "Jody", 33))
            log("after save")
        }

        // force the main thread wait
//        Thread.sleep(2000)
//        runBlocking { job.join() }

        log("Done.")
    }
}


