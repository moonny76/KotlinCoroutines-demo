package com.scarlet.coroutines.testing.intro

import com.google.common.truth.Truth.assertThat
import com.scarlet.model.Article
import com.scarlet.util.log
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.ContinuationInterceptor
import kotlin.system.measureTimeMillis

@ExperimentalCoroutinesApi
class T01_RunBlockingVsRunTest {

    interface ArticleService {
        suspend fun getArticle(id: String): Article
    }

    class Repository(private val articleService: ArticleService) {
        suspend fun getArticle(id: String): Article {
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
    fun `runBlocking demo`() = runBlocking {
        // Given
        coEvery {
            mockArticleService.getArticle(any())
        } coAnswers {
            delay(2000) // fake network delay
            expectedArticle
        }

        val duration = measureTimeMillis {
            // When
            val article = repository.getArticle("A001")
            // Then
            assertThat(article).isEqualTo(expectedArticle)
        }

        log("time elapsed = $duration")
    }

    @Test
    fun `runTest demo`() = runTest {
        val dispatcher: TestDispatcher = coroutineContext[ContinuationInterceptor] as TestDispatcher
        log("${dispatcher}")
        log("${dispatcher.scheduler}")

        // Given
        coEvery {
            mockArticleService.getArticle(any())
        } coAnswers {
            delay(2000) // fake network delay
            expectedArticle
        }

        val duration = measureTimeMillis {
            // When
            val article = repository.getArticle("A001")
            // Then
            assertThat(article).isEqualTo(expectedArticle)
        }

        log("time elapsed = $duration")
    }

}