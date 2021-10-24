package com.org.scarlet.coroutines.exceptions

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@JvmInline
value class Image(val content: String)

interface ImageApiService {
    suspend fun loadImage(name: String): Image
}

fun combineImages(image1: Image, image2: Image): Image =
    Image("${image1.content}, ${image2.content} combined")


@ExperimentalCoroutinesApi
class StructuredConcurrencyTest {

    @MockK(relaxed = true)
    lateinit var imageApiService: ImageApiService

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun loadAndCombineImages() = runBlockingTest{
        coEvery { imageApiService.loadImage(any()) } coAnswers {
            delay(1000)
            Image("image1")
        } coAndThen {
            delay(2000)
            Image("image2")
        }

        var image: Image? = null
        val job = launch {
            image = coroutineScope {
                val deferred1 = async { imageApiService.loadImage("image1") }.apply {
                    invokeOnCompletion { println("first loadImage caught $it") }
                }
                val deferred2 = async { imageApiService.loadImage("image2") }.apply {
                    invokeOnCompletion { println("second loadImage caught $it") } }
                combineImages(deferred1.await(), deferred2.await())
            }
        }

        job.cancelAndJoin()

        println(image)
        assertThat(image).isNull()
    }
}

