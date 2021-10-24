package com.org.scarlet.coroutines.basics

import com.org.scarlet.model.User
import com.org.scarlet.util.log
import com.org.scarlet.util.logi
import kotlinx.coroutines.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.EmptyCoroutineContext

suspend fun save(user: User) {
    // simulate network delay
    delay(1000)
    println("\tUser saved")
}

fun someFunc(block: StringBuilder.() -> Unit): Unit {
    val ss = StringBuilder()
    ss.block()
}

object Foo {
    @JvmStatic
    fun main(args: Array<String>) {
        someFunc {

        }
    }
}

object LaunchDemo01 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        logi(0, "1. before launch")

        val user = User("A001", "Jody", 33)

        launch {
            logi(1, "3. before save")
            save(user)
            logi(1, "4. after save")
        }

        logi(0, "2. after launch")
    }

}

object Launch_Demo02 {
    @JvmStatic
    fun main(args: Array<String>) {

        runBlocking {
            launch {
                delay(1000)
                logi(1, "(2) child 1 done.")
            }
            launch {
                delay(2000)
                logi(1, "(3) child 2 done.")
            }

            logi(0, "(1) end of runBlocking")
        }

        println("(4) Done")
    }
}

object Launch_Join_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        logi(0, "(1) start of runBlocking")

        launch {
            logi(1, "(2) child 1 start")
            delay(1000)
            logi(1, "(3) child 1 done")
        }

        println("(4) Done") // How to print this at the last
    }
}

@DelicateCoroutinesApi
object GlobalScope_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        logi(0, "(1) start of runBlocking")

        val user = User("A001", "Jody", 33)

        GlobalScope.launch {
            logi(1, "(2) before save")
            save(user)
            logi(1, "(3) after save")
        }.join()

        println("(4) Done.")
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
            logi(1, "before save")
            save(user)
            logi(1, "after save")
        }

//        runBlocking { job.join() }
//        Thread.sleep(2000) // force the main thread wait
        println("Done.")
    }
}


