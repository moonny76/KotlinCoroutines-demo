package com.scarlet.coroutines.testing.intro

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Test

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
suspend fun loadData(api: Api): String = withTimeout(5_000) {
    api.fetch()
}

@ExperimentalCoroutinesApi
class T04_Timeout02Test {

    @Test(expected = TimeoutCancellationException::class)
    fun test1() = runTest {

        val api = SuspendingFakeApi()

        log("result = ${loadData(api)}") // already timeout ...
    }

    @Test
    fun `check timeout cancellation demo`() = runTest {
        val api = SuspendingFakeApi()

        launch {
            log("result = ${loadData(api)}")
        }.onCompletion("job")

        advanceTimeBy(4_999) // 4999 ~ 5001
        api.deferred.complete("Hello")

        log("Done.")
    }
}