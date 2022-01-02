package com.scarlet.coroutines.advanced

import com.scarlet.util.coroutineInfo
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing
import java.util.concurrent.Executors

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

    suspend fun getFollowersNumber(): Int {
        delay(100)
        throw Error("Service exception")
    }

    suspend fun getUserName(): String {
        delay(500)
        return "paulaabdul"
    }

    suspend fun getTweets(): List<Tweet> {
        delay(500)
        return listOf(Tweet("Hello, world"))
    }

    suspend fun CoroutineScope.getUserDetails(): Details {
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
        println("User: $details")
        println("Tweets: ${tweets.await()}")
    }
// Only Exception...
}

object coroutineScopeDemo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val a = coroutineScope {
            delay(1000)
            10
        }
        println("a is calculated")
        val b = coroutineScope {
            delay(1000)
            20
        }
        println(a) // 10
        println(b) // 20
    }
// (1 sec)
// a is calculated
// (1 sec)
// 10
// 20
}

object What_We_Want {
    data class Details(val name: String, val followers: Int)
    data class Tweet(val text: String)

    class ApiException(val code: Int, message: String) : Throwable(message)

    suspend fun getFollowersNumber(): Int {
        delay(100)
        throw ApiException(500, "Service unavailable")
    }

    suspend fun getUserName(): String {
        delay(500)
        return "paulaabdul"
    }

    suspend fun getTweets(): List<Tweet> {
        delay(500)
        return listOf(Tweet("Hello, world"))
    }

    suspend fun getUserDetails(): Details = coroutineScope {
        val userName = async { getUserName() }
        val followersNumber = async { getFollowersNumber() }
        Details(userName.await(), followersNumber.await())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val details = try {
            getUserDetails()
        } catch (e: ApiException) {
            null
        }
        val tweets = async { getTweets() }
        println("User: $details")
        println("Tweets: ${tweets.await()}")
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
                println("\t\tInside coroutineScope")
                coroutineInfo(1)
                delay(100)
            }

            withContext(CoroutineName("child 1") + Dispatchers.Default) {
                println("\t\tInside first withContext")
                coroutineInfo(1)
                delay(500)
            }

            Executors.newFixedThreadPool(3).asCoroutineDispatcher().use { ctx ->
                withContext(CoroutineName("child 2") + ctx) {
                    println("\t\tInside second withContext")
                    coroutineInfo(1)
                    delay(1000)
                }
            }
        }

        delay(50)
        println("children after 50ms  = ${parent.children.toList()}")
        delay(200)
        println("children after 200ms = ${parent.children.toList()}")
        delay(600)
        println("children after 400ms = ${parent.children.toList()}")
        parent.join()
    }
}

object MainSafety_Demo {

    suspend fun fibonacci(n: Long): Long =
        withContext(Dispatchers.Default) {
            coroutineInfo(1)
            fib(n)
        }

    fun fib(n: Long): Long = if (n <= 1) n else fib(n - 1) + fib(n - 2)

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        launch(CoroutineName("parent") + Dispatchers.Swing) {
            coroutineInfo(0)

            println("fib(40) = ${fibonacci(40)}")

            coroutineInfo(0)
        }.join()
    }
}

object Timeout {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit>{
        launch {
            launch { // cancelled by its parent
                delay(2000)
                println("Will not be printed")
            }
            withTimeout(1000) { // we cancel launch
                delay(1500)
            }
        }.onCompletion("child 1")

        launch {
            delay(2000)
            println("Done")
        }.onCompletion("child 2")
    }
// (2 sec)
// Done
}

object WithTimeoutOrNull_Demo {
    class User()

    suspend fun fetchUser(): User {
        // Runs forever
        while (true) {
            yield()
        }
    }

    suspend fun getUserOrNull(): User? =
        withTimeoutOrNull(1000) {
            fetchUser()
        }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        val user = getUserOrNull()
        println("User: $user")
    }
// (1 sec)
// User: null
}