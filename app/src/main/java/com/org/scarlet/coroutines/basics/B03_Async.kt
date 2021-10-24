package com.org.scarlet.coroutines.basics

import com.org.scarlet.model.User
import com.org.scarlet.util.logi
import kotlinx.coroutines.*

object Async_Demo1 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking{

        val deferred = async {
            delay(1000)
            42
        }

        println(deferred.await())
    }
}

object Async_Demo2 {

    private suspend fun getUser(userId: String): User {
        logi(1, "inside getUser")
        delay(3_000)
        return User("A001", "Sara Corner", 33)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val user = async {
            logi(1, "Request user with Id A001")
            getUser("A001")
        }

        println("Waiting for results ...")
        println("${user.await()}")
        println("Done")
    }
}

object Async_Demo3 {

    private suspend fun getUser(userId: String): User {
        logi(1, "inside getUser")
        delay(3_000)
        return User("A001", "Sara Corner", 33)
    }

    // DON'T DO THIS
    @DelicateCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val user = GlobalScope.async {
            logi(1, "Request user with Id A001")
            getUser("A001")
        }

        println("Waiting for results ...")
        println("${user.await()}")
        println("Done")
    }
}