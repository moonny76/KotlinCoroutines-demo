package com.scarlet.coroutines.basics

import com.scarlet.model.User
import com.scarlet.util.log
import kotlinx.coroutines.*

suspend fun save(user: User) {
    // simulate network delay
    delay(1000)
    log("\tUser saved")
}

object LaunchDemo01 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log(0, "1. before launch")

        val user = User("A001", "Jody", 33)

        launch {
            log(1, "3. before save")
            save(user)
            log(1, "4. after save")
        }

        log(0, "2. after launch")
    }

}

object Launch_Demo02 {
    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {
            launch {
                delay(1000)
                log(1, "(2) child 1 done.")
            }
            launch {
                delay(2000)
                log(1, "(3) child 2 done.")
            }

            log(0, "(1) end of runBlocking")
        }

        log("(4) Done")
    }
}

object Launch_Join_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        log(0, "(1) start of runBlocking")

        launch {
            log(1, "(2) child 1 start")
            delay(1000)
            log(1, "(3) child 1 done")
        }

        log("(4) Done") // How to print this at the last
    }
}

@DelicateCoroutinesApi
object GlobalScope_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        log(0, "(1) start of runBlocking")

        val user = User("A001", "Jody", 33)

        GlobalScope.launch {
            log(1, "(2) before save")
            save(user)
            log(1, "(3) after save")
        }.join()

        log("(4) Done.")
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
            log(1, "before save")
            save(user)
            log(1, "after save")
        }

//        runBlocking { job.join() }
//        Thread.sleep(2000) // force the main thread wait
        log("Done.")
    }
}


