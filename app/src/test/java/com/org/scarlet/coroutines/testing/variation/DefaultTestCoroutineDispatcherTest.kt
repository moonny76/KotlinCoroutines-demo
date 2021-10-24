package com.org.scarlet.coroutines.testing.variation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.org.scarlet.livedata.ApiService
import com.org.scarlet.model.Article
import com.org.scarlet.util.Resource
import com.org.scarlet.util.getValueForTest
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.ContinuationInterceptor

@ExperimentalCoroutinesApi
class DefaultTestCoroutineDispatcherTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testArticles = Resource.Success(Article.articleSamples)

    @MockK
    private lateinit var apiService: ApiService

    // SUT
    private lateinit var viewModel: ArticleViewModel

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    // Define `testDispatcher` as TestCoroutineScope's extension property
    private val TestCoroutineScope.testDispatcher: TestCoroutineDispatcher
        get() = TODO()
    @Test
    fun `onButtonClicked - test fun creating new coroutines`() = runBlockingTest {
        // Given
        viewModel = ArticleViewModel(apiService, testDispatcher)

        coEvery { apiService.getArticles() } coAnswers {
            delay(3000)
            testArticles
        }

        // When
        viewModel.onButtonClicked()

        delay(3010) // should set > 3000
        println("$currentTime")

        coVerify { apiService.getArticles() }

        val articles = viewModel.articles.getValueForTest()
        assertThat(articles).isEqualTo(testArticles)
    }


    @Test
    fun `onButtonClicked - test fun creating new coroutines - pause & resume`() = runBlockingTest {
        // Given
        viewModel = ArticleViewModel(apiService, testDispatcher)

        coEvery { apiService.getArticles() } coAnswers {
            delay(3000)
            testArticles
        }

        // When
        println(Thread.currentThread().name)

        pauseDispatcher()
        viewModel.onButtonClicked()

        resumeDispatcher()

        // Then
        coVerify { apiService.getArticles() }

        val articles = viewModel.articles.getValueForTest()
        assertThat(articles).isEqualTo(testArticles)
    }

}