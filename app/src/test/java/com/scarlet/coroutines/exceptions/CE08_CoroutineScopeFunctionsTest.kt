package com.scarlet.coroutines.exceptions

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Test

class CoroutineScopeFunctionsTest {

    @Test
    fun withContext_demo1() = runTest {
        try {
            withContext(Dispatchers.Default) {
                coroutineContext.job.onCompletion("withContext")

                delay(1_000)
                throw RuntimeException("Exception in withContext")
            }
        } catch (e: Exception) {
            println("Caught $e")
        }
    }

    @Test
    fun withContext_demo2() = runTest {
        log("runTest: $coroutineContext")

        try {
            withContext(SupervisorJob()) { // pointless
                coroutineContext.job.onCompletion("withContext")

                delay(1_000)
                launch {
                    delay(1_000)
                    throw RuntimeException("Exception in withContext")

                }.onCompletion("child")
            }
        } catch (e: Exception) {
            println("Caught $e")
        }
    }

    @Test
    fun withContext_demo3() = runTest {
        log("runTest: $coroutineContext")

        try {
            supervisorScope {
                coroutineContext.job.onCompletion("supervisorScope")

                delay(1_000)
                launch {
                    delay(1_000)
                    throw RuntimeException("Exception in withContext")

                }.onCompletion("child")
            }
        } catch (e: Exception) {
            println("Caught $e")
        }
    }
}