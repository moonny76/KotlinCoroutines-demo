package com.org.scarlet.coroutines.basics

import kotlinx.coroutines.*
import java.lang.RuntimeException

@JvmInline
private value class Image(val name: String)

private suspend fun loadImage(name: String): Image {
    println("Loading $name image started.")
    delay(1000)
    println("Loading $name image done.")
    return Image(name)
}

private suspend fun loadImageFail(name: String): Image {
    println("Loading $name image started.")
    delay(500)
    throw RuntimeException("oops")
}

private fun combineImages(image1: Image, image2: Image): Image =
    Image("${image1.name} & ${image2.name}")

@DelicateCoroutinesApi
private suspend fun loadAndCombine(name1: String, name2: String): Image {
    val deferred1 = GlobalScope.async { loadImage(name1) }
    val deferred2 = GlobalScope.async { loadImage(name2) }
    return combineImages(deferred1.await(), deferred2.await())
}

@DelicateCoroutinesApi
object Using_GlobalScope_But_Not_Recommended {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null

        val parent = GlobalScope.launch {
            image = loadAndCombine("apple", "kiwi")
            println("parent done.")
        }

        parent.join()
        println("combined image = $image")
    }
}

@DelicateCoroutinesApi
object Using_GlobalScope_Even_If_Parent_Cancelled_Children_Keep_Going {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null

        val parent = GlobalScope.launch {
            image = loadAndCombine("apple", "kiwi")
            println("parent done.")
        }

        delay(500)

        parent.cancelAndJoin()
        println("combined image = $image")

        delay(1000) // To check what happens to children
    }
}

/**
 * Pass the parent coroutine scope
 */

// See what will happen if we use GlobalScope
object Parent_Cancellation_When_Passing_Coroutine_Scope_As_Parameter {

    private suspend fun loadAndCombine(scope: CoroutineScope, name1: String, name2: String): Image {
        val deferred1 = scope.async { loadImage(name1) }
        val deferred2 = scope.async { loadImage(name2) }
        return combineImages(deferred1.await(), deferred2.await())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null
        val scope = CoroutineScope(Job())

        val parent = scope.launch {
            image = loadAndCombine(this, "apple", "kiwi")
            println("Parent done")
        }

        delay(500)

        parent.cancelAndJoin()
        println("combined image = $image")

        delay(1000) // To check what happens to children just in case
    }
}

// See what will happen if we use GlobalScope
object Child_Failure_When_Passing_Coroutine_Scope_As_Parameter {

    private suspend fun loadAndCombine(scope: CoroutineScope, name1: String, name2: String): Image {
        val deferred1 = scope.async { loadImageFail(name1) }
        val deferred2 = scope.async { loadImage(name2) }
        return combineImages(deferred1.await(), deferred2.await())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null
        val scope = CoroutineScope(Job())

        val parent = scope.launch {
            image = loadAndCombine(this, "apple", "kiwi")
            println("Parent done")
        }

        parent.join()
        println("combined image = $image")

        delay(1000) // To check what happens to children just in case
    }

}

/**
 * Use coroutineScope()
 */

object coroutineScope_applied_and_parent_cancelled {

    private suspend fun loadAndCombine(name1: String, name2: String): Image = coroutineScope {
        val deferred1 = async { loadImage(name1) }
        val deferred2 = async { loadImage(name2) }
        combineImages(deferred1.await(), deferred2.await())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null
        val scope = CoroutineScope(Job())

        val parent = scope.launch {
            image = loadAndCombine("apple", "kiwi")
            println("Parent done.")
        }

        delay(500)

        parent.cancelAndJoin()
        println("combined image = $image")

        delay(1000) // To check what happens to children just in case
    }

}

object coroutineScope_applied_and_child_failed {

    private suspend fun loadAndCombine(name1: String, name2: String): Image = coroutineScope {
        val deferred1 = async { loadImageFail(name1) }
        val deferred2 = async { loadImage(name2) }
        combineImages(deferred1.await(), deferred2.await())
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null
        val scope = CoroutineScope(Job())

        val parent = scope.launch {
            image = loadAndCombine("apple", "kiwi")
            println("Parent done.")
        }

        parent.join()
        println("combined image = $image")

        delay(1000) // To check what happens to children just in case
    }

}
