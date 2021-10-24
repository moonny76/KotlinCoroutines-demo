package com.org.scarlet.coroutines.testing.intro

import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import kotlin.coroutines.ContinuationInterceptor

interface Api {
    suspend fun fetch(): String
}

private class SuspendingFakeApi : Api {
    val deferred = CompletableDeferred<String>()

    override suspend fun fetch(): String {
        return deferred.await() // wait forever ...
    }
}

@ExperimentalCoroutinesApi
suspend fun loadData(api: Api): String {
    println("timeout start ...")
    val result = withTimeout(5_000) {
        api.fetch()
    }
    println("fetch returned")
    return result
}

@ExperimentalCoroutinesApi
class F01_TimeControlTest {

    @Test(expected = TimeoutCancellationException::class)
    fun test1() = runBlockingTest {

        val api = SuspendingFakeApi()

        println("result = ${loadData(api)}") // already timeout ...
    }

    @Test
    fun `success within timeout`() = runBlockingTest {
        val api = SuspendingFakeApi()

        launch {
            println("result = ${loadData(api)}")
        }.invokeOnCompletion { ex ->
            println("exception = $ex, ${coroutineContext[ContinuationInterceptor]}")
        }

        advanceTimeBy(4999)
        api.deferred.complete("Hello")

        println("Done.")
    }

    @Test
    fun `timeout cancellation exception`() = runBlockingTest {
        val api = SuspendingFakeApi()

        launch {
            println("result = ${loadData(api)}")
        }.invokeOnCompletion { ex ->
            println("exception = $ex, ${coroutineContext[ContinuationInterceptor]}")
        }

        println("Done.")
    }
}