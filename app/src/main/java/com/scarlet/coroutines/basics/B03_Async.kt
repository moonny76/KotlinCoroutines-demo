package com.scarlet.coroutines.basics

import com.scarlet.model.User
import com.scarlet.util.log
import kotlinx.coroutines.*

private suspend fun getUser(userId: String): User {
    log("inside getUser $userId")
    delay(1_000)
    return User("A001", "Sara Corner", 33)
}

object Async_Demo1 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {

        val user = async {
            log("Request user with Id A001")
            getUser("A001")
        }

        log("Waiting for results ...")
//        log(user.await())
        log("Done")
    }
}

// DON'T DO THIS
@DelicateCoroutinesApi
object Async_Demo2 {

    @ExperimentalStdlibApi
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