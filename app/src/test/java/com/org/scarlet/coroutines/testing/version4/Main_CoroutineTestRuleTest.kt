package com.org.scarlet.coroutines.testing.version4

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
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

@ExperimentalCoroutinesApi
class Main_CoroutineTestRuleTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testArticles = Resource.Success(Article.articleSamples)

    @MockK
    private lateinit var apiService: ApiService

    // SUT
    private lateinit var viewModel: ArticleViewModel

    @get:Rule
    val coroutineRule = MainCoroutineTestRule()

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `runBlockingTest - test fun creating new coroutines`() = coroutineRule.runBlockingTest {
        // Given
        viewModel = ArticleViewModel(apiService)

        coEvery { apiService.getArticles() } coAnswers {
            delay(3000)
            testArticles
        }

        // When
        pauseDispatcher()
        viewModel.onButtonClicked()

        resumeDispatcher()
        println("current time = $currentTime")

        // Then
        coVerify { apiService.getArticles() }

        val articles = viewModel.articles.getValueForTest()
        Truth.assertThat(articles).isEqualTo(testArticles)
    }

}