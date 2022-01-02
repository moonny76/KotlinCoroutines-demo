package com.scarlet.coroutines.exceptions

import com.google.common.truth.Truth.assertThat
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.lang.RuntimeException

@JvmInline
value class Image(val content: String)

interface ApiService {
    suspend fun loadImage(name: String): Image
}

fun combineImages(image1: Image, image2: Image): Image =
    Image("${image1.content}, ${image2.content} combined")


@ExperimentalCoroutinesApi
class StructuredConcurrencyTest {

    @MockK(relaxed = true)
    lateinit var apiService: ApiService

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `loadAndCombineImages - parent job cancelled`() = runTest {
        coEvery { apiService.loadImage(any()) } coAnswers  {
            delay(1000)
            Image("image1")
        } coAndThen {
            delay(2000)
            Image("image2")
        }

        var image: Image? = null
        val job = launch {
            image = coroutineScope {
                val deferred1 = async { apiService.loadImage("image1") }.onCompletion("deferred1")
                val deferred2 = async { apiService.loadImage("image2") }.onCompletion("deferred2")

                combineImages(deferred1.await(), deferred2.await())
            }
        }.onCompletion("parent")

        delay(1500)
        job.cancelAndJoin()

        log(image.toString())
        assertThat(image).isNull()
    }

    @Test
    fun `loadAndCombineImages - child fails`() = runTest {
        coEvery { apiService.loadImage(any()) } coAnswers  {
            delay(1000)
            throw RuntimeException("Oops")
        } coAndThen {
            delay(2000)
            Image("image2")
        }

        var image: Image? = null
        launch {
            try {
                image = coroutineScope {
                    val deferred1 =
                        async { apiService.loadImage("image1") }.onCompletion("deferred1")
                    val deferred2 =
                        async { apiService.loadImage("image2") }.onCompletion("deferred2")

                    combineImages(deferred1.await(), deferred2.await())
                }
            } catch (ex: Exception) {
                log("Caught ex = $ex")
            }
        }.onCompletion("parent").join()

        log(image.toString())
        assertThat(image).isNull()
    }
}

