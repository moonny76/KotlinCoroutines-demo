package com.org.scarlet.coroutines.advanced

import com.org.scarlet.util.coroutineInfo
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.Executors
import kotlin.random.Random

/**
 * Dispatchers:
 *      1. Dispatchers.Main
 *      2. Dispatchers.IO
 *      3. Dispatchers.Default
 *      4. Dispatchers.Unconfined (not recommended)
 */

/**
 * Exception in thread "main @coroutine#1" java.lang.IllegalStateException:
 * Module with the Main dispatcher had failed to initialize.
 */
object Dispatchers_Main_Failure_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val scope = CoroutineScope(Job() + Dispatchers.Main)

        scope.launch {
            delay(1000)
        }.join()

        println("Done.")
    }
}

object Other_Dispatchers_Demo {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        coroutineInfo(0)

        launch(Dispatchers.Default) {
            coroutineInfo(1)
            delay(1000)
        }.join()

        launch(Dispatchers.IO) {
            coroutineInfo(1)
            delay(1000)
        }.join()

//        launch(Dispatchers.Unconfined + CoroutineName("Unconfined")) {
//            coroutineInfo(1)
//            withContext(Dispatchers.Default + CoroutineName("Default")) {
//                coroutineInfo(2)
//                delay(1000)
//            }
//            // Whatever thread the suspending function uses will be continue to run
//            coroutineInfo(1)
//        }.join()

        println("Done.")
    }
}

object DefaultDispatchers_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        repeat(20) {
            launch(Dispatchers.Default) {
                // To make it busy
                List(1000) { Random.nextLong() }.maxOrNull()

                println("Running on thread: ${Thread.currentThread().name}")
            }
        }
    }
}

object IODispatchers_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{
        repeat(20) {
            launch(Dispatchers.IO) {
                Thread.sleep(200)

                println("Running on thread: ${Thread.currentThread().name}")
            }
        }
    }
}

/**
 * IO dispatcher shares threads with a Dispatchers.Default dispatcher, so using
 * withContext(Dispatchers.IO) { ... } does not lead to an actual switching to another thread.
 */
object ThreadSharing_Demo {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit>{
        launch(Dispatchers.Default) {
            println(Thread.currentThread().name)

            withContext(Dispatchers.IO) {
                println(Thread.currentThread().name)
            }
        }
    }
}
/**
 * newSingleThreadContext and newFixedThreadPoolContext (Both will be deprecated!)
 */
@ExperimentalCoroutinesApi
object Custom_Dispatchers_Demo {

    @ObsoleteCoroutinesApi
    @JvmStatic
    fun main(args: Array<String>) = runBlocking<Unit> {

        val context = newSingleThreadContext("CustomDispatcher 1")
        launch(context) {
            coroutineInfo(0)
            delay(100)
        }.join()
        context.close() // make sure to close

        // Safe way
        newSingleThreadContext("CustomDispatcher 2").use { ctx ->
            launch(ctx) {
                coroutineInfo(0)
            }.join()
        }

        val context1 = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        launch(context1) {
            coroutineInfo(0)
        }.join()
        context1.close() // make sure to close

        /* TODO */
        // Use use {}
    }

}

