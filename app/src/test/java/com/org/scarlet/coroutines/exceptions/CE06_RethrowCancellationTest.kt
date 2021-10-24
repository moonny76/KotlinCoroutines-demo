package com.org.scarlet.coroutines.exceptions

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class CE06_RethrowCancellationTest {

    private suspend fun networkRequestCooperative() {
        println("networkRequestCooperative start")
        delay(1_000)
        println("networkRequestCooperative done")
    }

    private suspend fun networkRequestFailed() {
        delay(500)
        throw RuntimeException("Oops...from networkRequestFailed")
    }

    private suspend fun networkRequestUncooperative() {
        println("networkRequestUncooperative start")
        delay(0)
        println(fib(45))
        println("networkRequestUncooperative done")
    }

    private fun fib(n: Long): Long = if (n <= 1) n else fib(n - 1) + fib(n - 2)

    /**/

    @Test
    fun `uncooperative coroutine cannot be cancelled`() = runBlocking {
        val job = launch {
            networkRequestUncooperative()
        }.apply { invokeOnCompletion { println("job: exception = $it") }}

        delay(100)

        job.cancelAndJoin()
    }

    @Test
    fun `cooperative coroutine can be canceled`() = runBlocking {
        val job = launch {
            networkRequestCooperative()
        }.apply { invokeOnCompletion { println("job: exception = $it") }}

        delay(100)

        job.cancel()
    }

    @Test
    fun `failed suspending function throws causing exception, not cancellation exception`() = runBlocking{
        launch {
            try {
                networkRequestFailed()
            } catch(ex: Exception) {
                println("Caught exception = $ex")
            }
        }.join()
    }

    /**
     * Check to see what happens when coroutine is cancelled
     *
     * Cancelled coroutine sets its status as `Cancelling`, and executes
     * rest of the computation (probably call cancel() to child coroutines if exist).
     * If Cancellation exception is thrown from any computation, it skips
     * the rest of the computation immediately!!!!!!!!!!!!!!!!!!!!!!
     */

    // DO NOT CATCH CANCELLATION EXCEPTION!! IF YOU HAPPEN TO CATCH IT, RETHROW IT!!
    @Test
    fun `cancellation exception swallowed - so, next suspend function starts running`() = runBlocking {
        val job = launch {
            println("Coroutine starts running ... isActive = ${(coroutineContext[Job]?.isActive)}")

            try {
                networkRequestCooperative()
            } catch (ex: Exception) {
                println("Caught: $ex") // CancellationException swallowed
            }

            println("Coroutine keep running ... isActive = ${(coroutineContext[Job]?.isActive)}")
            networkRequestUncooperative()

        }.apply { invokeOnCompletion { println("job: exception = $it") }}

        delay(100)

        job.cancel()
    }

    @Test
    fun `cancellation exception swallowed - but, next cooperative suspend function will cancel`() = runBlockingTest {

        val job = launch {
            try {
                networkRequestCooperative()
            } catch (ex: Exception) {
                println("Caught: $ex") // CancellationException swallowed
            }

            println("Second suspend function will cancel, though")

            try {
                networkRequestCooperative()
            } catch (ex: Exception) {
                if (ex is CancellationException) {
                    throw ex
                }
            }

            println("I am skipped...")

        }.apply { invokeOnCompletion { println("job: exception = $it") }}


        delay(100)

        job.cancel()
    }

    @Test
    fun `cancellation caught, but rethrown - remaining computation all skipped`() = runBlockingTest {

        val job = launch {
            try {
                networkRequestCooperative()
            } catch (ex: Exception) {
                println("Caught: $ex")
                if (ex is CancellationException) {
                    throw ex
                }
            }

            println("All subsequent computations will be skipped ...")

            networkRequestCooperative()
            networkRequestUncooperative() // long running computation

        }.apply { invokeOnCompletion { println("job: exception = $it") }}

        delay(100)

        job.cancel()
    }

}