package com.scarlet.coroutines.advanced

import com.scarlet.util.coroutineInfo
import com.scarlet.util.delim
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * CoroutineContext:
 *  1. Coroutine Job
 *  2. Coroutine Dispatcher
 *  3. Coroutine Exception Handler
 *  4. Coroutine Name
 *  5. Coroutine Id (Only if debug mode is ON: -Dkotlinx.coroutines.debug)
 */

object CoroutineContext_01 {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println(Thread.currentThread().name)
        println("CoroutineContect  = $coroutineContext")
        println("Name              = ${coroutineContext[CoroutineName]}")
        println("Job               = ${coroutineContext[Job]}")
        println("Dispatcher        = ${coroutineContext[ContinuationInterceptor]}")
        println("Exception handler = ${coroutineContext[CoroutineExceptionHandler]}")
    }
}

object CoroutineContext_Creation_Plus {
    @JvmStatic
    fun main(args: Array<String>) {
//        val context: CoroutineName = CoroutineName("My Coroutine")
//        val context: CoroutineContext.Element = CoroutineName("My Coroutine")
        var context: CoroutineContext = CoroutineName("My Coroutine")
        println(context)

        context += Dispatchers.Default
        println(context)

        context += Job()
        println(context)
    }
}

object CoroutineContext_Merge {
    @JvmStatic
    fun main(args: Array<String>) {
        var context = CoroutineName("My Coroutine") + Dispatchers.Default + Job()
        println(context)
        delim();

        /*
         * Element on the right overrides the same element on the left.
         */

        context += CoroutineName("Your Coroutine") + Dispatchers.IO + SupervisorJob()

        println(context)
        delim()

        /*
         * Empty CoroutineContext
         */

        val emptyContext = EmptyCoroutineContext

        context += emptyContext

        println(context)
        delim()

        /*
         * Minus Key demo
         */

        context = context.minusKey(ContinuationInterceptor)

        println(context)
        delim()
    }
}

object CoroutineContext_Fold {
    @JvmStatic
    fun main(args: Array<String>) {
        val context = CoroutineName("My Coroutine") + Dispatchers.Default + Job()

        context.fold("") { acc, elem ->
            "$acc : $elem"
        }.also(::println)

        context.fold(emptyList<CoroutineContext>()) { acc, elem ->
            acc + elem
        }.joinToString().also(::println)
    }
}

object CoroutineContext_ContextInheritance_Demo {

    @JvmStatic
    fun main(args: Array<String>) {
        println("top-level thread = ${Thread.currentThread().name}")

        // The default context is an event loop on the current thread.
        runBlocking(CoroutineName("Parent Coroutine: runBlocking")) {
            coroutineInfo(1)

            // Inherits context from parent scope. If no inherited dispatcher, use Dispatchers.DEFAULT.
            launch {
//            launch(CoroutineName("Child Coroutine: launch") + Dispatchers.Default) {
                coroutineInfo(2)
                delay(1000)
            }.join()

            println("\trunBlocking: try to exit runBlocking")
        }
        println("Bye main")
    }
}
