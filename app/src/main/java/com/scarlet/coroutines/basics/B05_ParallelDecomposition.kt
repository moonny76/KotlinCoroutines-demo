package com.scarlet.coroutines.basics

import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.NonDisposableHandle.parent
import java.lang.RuntimeException

@JvmInline
private value class Image(val name: String)

private suspend fun loadImage(name: String): Image {
    log("Loading ${name}: started.")
    delay(1000)
    log("Loading ${name}: done.")
    return Image(name)
}

private suspend fun loadImageFail(name: String): Image {
    log("Loading ${name}: started.")
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
object GlobalScope_But_Not_Recommended {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null

        val parent = GlobalScope.launch {
            image = loadAndCombine("apple", "kiwi")
            log("parent done.")
        }

        parent.join()
        log("combined image = $image")
    }
}

@DelicateCoroutinesApi
object GlobalScope_Even_If_Parent_Cancelled_Children_Keep_Going {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null

        val parent = GlobalScope.launch {
            image = loadAndCombine("apple", "kiwi")
            log("parent done.")
        }

        delay(500)

        parent.cancelAndJoin()
        log("combined image = $image")

//        delay(1000) // To check what happens to children
    }
}

@DelicateCoroutinesApi
private suspend fun loadAndCombineFail(name1: String, name2: String): Image {
    val deferred1 = GlobalScope.async { loadImageFail(name1) }
    val deferred2 = GlobalScope.async { loadImage(name2) }
    return combineImages(deferred1.await(), deferred2.await())
}

@DelicateCoroutinesApi
object GlobalScope_EvenIf_One_Of_Children_Fails_Other_Child_Still_Runs {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        var image: Image? = null

        val parent = GlobalScope.launch {
            try {
                image = loadAndCombineFail("apple", "kiwi")
            } catch (e: Exception) {
                log("parent caught $e")
            }
            log("parent done.")
        }

        parent.join()
        log("combined image = $image")

        delay(1000) // To check what happens to children
    }
}

/**
 * Pass the parent coroutine scope
 */

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
            log("Parent done")
        }

        parent.join()
//        delay(500)
//        parent.cancelAndJoin()

        log("combined image = $image")

        delay(1000) // To check what happens to children just in case
    }
}

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
            log("Parent done")
        }

        parent.join()
        log("combined image = $image")

        delay(1000) // To check what happens to children just in case
    }

}

/**
 * Use coroutineScope()
 */

object Using_coroutineScope_and_when_parent_cancelled {

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
            log("Parent done.")
        }

        parent.join()
//        delay(500)
//        parent.cancelAndJoin()

        log("combined image = $image")

        delay(1000) // To check what happens to children just in case
    }

}

object Using_coroutineScope_and_when_child_failed {

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
            log("Parent done.")
        }

        parent.join()
        log("combined image = $image")

        delay(1000) // To check what happens to children just in case
    }

}