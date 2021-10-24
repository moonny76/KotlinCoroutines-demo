package com.org.scarlet.coroutines.testing.intro

import com.google.common.truth.Truth.assertThat
import com.org.scarlet.model.Article
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
class T01_RunBlockingVsRunBlockingTest {

    interface ArticleService {
        suspend fun getArticle(id: String): Article
    }

    class Repository(private val articleService: ArticleService) {
        suspend fun getArticle(id: String): Article {
            delay(2_000) // fake network delay
            return articleService.getArticle(id)
        }
    }

    // SUT
    private lateinit var repository: Repository

    private val expectedArticle = Article("A001", "Roman Elizarov", "Kotlin Coroutines")

    @MockK
    private lateinit var mockArticleService: ArticleService

    @Before
    fun init() {
        MockKAnnotations.init(this)
        repository = Repository(mockArticleService)
    }

    @Test
    fun `networkRequest - test suspend fun not creating new coroutines - runBlocking`() =
        runBlocking {
            // Given
            coEvery { mockArticleService.getArticle(any()) } returns expectedArticle

            val duration = measureTimeMillis {
                // When
                val article = repository.getArticle("A001")
                // Then
                assertThat(article).isEqualTo(expectedArticle)
            }

            println("time elapsed = $duration")
        }

    @Test
    fun `networkRequest - test suspend fun not creating new coroutines - runBlockingTest`() =
        runBlockingTest {
            // Given
            coEvery { mockArticleService.getArticle(any()) } answers {
                expectedArticle
            }

            val duration = measureTimeMillis {
                // When
                val article = repository.getArticle("A001")
                // Then
                assertThat(article).isEqualTo(expectedArticle)
            }

            println("time elapsed = $duration")
        }

    /**
     * This job has not completed yet
     */
    @Test
    fun `runBlockingTest - This job has not completed yet`() = runBlockingTest {
        // successful case
        launch {
            delay(1000)
            println("Yelling")
        }.join()

        // failing case
//        val scope = CoroutineScope(Job())
//
//        val job = scope.launch {
//            println("time in = $currentTime")
//            delay(1000)
//            println("Screaming")
//        }.join()

        println("Done")
    }

}