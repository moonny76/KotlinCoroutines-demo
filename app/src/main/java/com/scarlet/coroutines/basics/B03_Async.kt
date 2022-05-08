package com.scarlet.coroutines.basics

import com.scarlet.model.User
import com.scarlet.util.log
import kotlinx.coroutines.*

object Async_Demo1 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking{

        val deferred = async {
            delay(1000)
            42
        }

        log(deferred.await())
    }
}

object Async_Demo2 {

    private suspend fun getUser(userId: String): User {
        log("inside getUser")
        delay(1_000)
        return User("A001", "Sara Corner", 33)
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val user = async {
            log("Request user with Id A001")
            getUser("A001")
        }

        log("Waiting for results ...")
        log(user.await())
        log("Done")
    }
}

object Async_Demo3 {

    private suspend fun getUser(userId: String): User {
        log("inside getUser")
        delay(1_000)
        return User("A001", "Sara Corner", 33)
    }

    // DON'T DO THIS
    @DelicateCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val user = GlobalScope.async {
            log("Request user with Id A001")
            getUser("A001")
        }

        log("Waiting for results ...")
        log(user.await())
        log("Done")
    }
}