package com.scarlet.coroutines.advanced

import com.scarlet.util.coroutineInfo
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext

/**
 * Coroutine Scope Functions
 *
 * 1. coroutineScope
 * 2. supervisorScope
 * 3. withContext
 * 4. withTimeout
 * 5. withTimeoutOrNull
 */

object Not_What_We_Want {
    data class Details(val name: String, val followers: Int)
    data class Tweet(val text: String)

    private suspend fun getFollowersNumber(): Int {
        delay(100)
        throw Error("Service exception")
    }

    private suspend fun getUserName(): String {
        delay(500)
        return "paula abdul"
    }

    private suspend fun getTweets(): List<Tweet> {
        delay(500)
        return listOf(Tweet("Hello, world"))
    }

    // DON'T DO THIS
    private suspend fun CoroutineScope.getUserDetails(): Details {
        val userName = async { getUserName() }
        val followersNumber = async { getFollowersNumber() }
        return Details(userName.await(), followersNumber.await())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val details = try {
            getUserDetails()
        } catch (e: Error) {
            null
        }
        val tweets = async { getTweets() }
        log("User: $details")
        log("Tweets: ${tweets.await()}")
    }
// Only Exception...
}

object What_We_Want {
    data class Details(val name: String, val followers: Int)
    data class Tweet(val text: String)

    class ApiException(val code: Int, message: String) : Throwable(message)

    private suspend fun getFollowersNumber(): Int {
        delay(100)
        throw ApiException(500, "Service unavailable")
    }

    private suspend fun getUserName(): String {
        delay(500)
        return "paula abdul"
    }

    private suspend fun getTweets(): List<Tweet> {
        delay(500)
        return listOf(Tweet("Hello, world"))
    }

    private suspend fun getUserDetails(): Details = coroutineScope {
        val userName = async { getUserName() }
        val followersNumber = async { getFollowersNumber() }
        Details(userName.await(), followersNumber.await())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val details = try {
            getUserDetails()
        } catch (e: ApiException) {
            log("Error: ${e.code}")
            null
        }
        val tweets = async { getTweets() }
        log("User: $details")
        log("Tweets: ${tweets.await()}")
    }
// User: null
// Tweets: [Tweet(text=Hello, world)]
}

object withContext_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val parent = launch(CoroutineName("parent")) {
            coroutineInfo(0)

            coroutineScope {
                coroutineContext.job.onCompletion("coroutineScope")
                log("\t\tInside coroutineScope")
                coroutineInfo(1)
                delay(100)
            }

            withContext(CoroutineName("child 1") + Dispatchers.Default) {
                coroutineContext.job.onCompletion("withContext")
                log("\t\tInside first withContext")
                coroutineInfo(1)
                delay(500)
            }

            Executors.newFixedThreadPool(3).asCoroutineDispatcher().use { ctx ->
                withContext(CoroutineName("child 2") + ctx) {
                    coroutineContext.job.onCompletion("newFixedThreadPool")
                    log("\t\tInside second withContext")
                    coroutineInfo(1)
                    delay(1000)
                }
            }
        }.onCompletion("parent")

        delay(50)
        log("children after 50ms  = ${parent.children.toList()}")
        delay(200)
        log("children after 200ms = ${parent.children.toList()}")
        delay(600)
        log("children after 400ms = ${parent.children.toList()}")
        parent.join()
    }
}

object MainSafety_Demo {

    private suspend fun fibonacci(n: Long): Long =
        withContext(Dispatchers.Default) {
            fib(n).also {
                log(coroutineContext)
            }
        }

    private fun fib(n: Long): Long = if (n == 0L || n == 1L) n else fib(n - 1) + fib(n - 2)

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        launch(CoroutineName("parent") + Dispatchers.Swing) {
            log("fib(40) = ${fibonacci(40)}")
        }.join()
    }
}

object Timeout {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit>{
        launch {
            launch { // cancelled by its parent
                delay(2000)
                log("Will not be printed")
            }
            withTimeout(1000) { // we cancel launch
                delay(1500)
            }
        }.onCompletion("child 1")

        launch {
            delay(2000)
            log("child2 done")
        }.onCompletion("child 2")
    }
// (2 sec)
// Done
}

object WithTimeoutOrNull_Demo {
    class User

    private suspend fun fetchUser(): User {
        // Runs forever
        while (true) {
            yield()
        }
    }

    private suspend fun getUserOrNull(): User? =
        withTimeoutOrNull(3000) {
            fetchUser()
        }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val user = getUserOrNull()
        log("User: $user")
    }
// (3 sec)
// User: null
}