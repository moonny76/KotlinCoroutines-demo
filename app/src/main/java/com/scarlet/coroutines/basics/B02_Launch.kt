package com.scarlet.coroutines.basics

import com.scarlet.model.User
import com.scarlet.util.log
import kotlinx.coroutines.*

suspend fun save(user: User) {
    // simulate network delay
    delay(1000)
    log("\tUser saved $user")
}

object LaunchDemo01 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log("1. before launch")

        val user = User("A001", "Jody", 33)

        launch {
            log("3. before save")
            save(user)
            log("4. after save")
        }

        log("2. after launch")
    }

}

object Launch_Demo02 {
    @JvmStatic
    fun main(args: Array<String>) {
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

        log("4. Done") // How to print this at the last
    }
}

@DelicateCoroutinesApi
object GlobalScope_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        log("1. start of runBlocking")

        val user = User("A001", "Jody", 33)

        GlobalScope.launch {
            log("2. before save")
            save(user)
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

        val user = User("A001", "Jody", 33)

        scope.launch {
            log("before save")
            save(user)
            log("after save")
        }

//        runBlocking { job.join() }
//        Thread.sleep(2000) // force the main thread wait
        log("Done.")
    }
}


