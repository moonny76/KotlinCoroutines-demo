package com.org.scarlet.coroutines.testing.version2

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

@ExperimentalCoroutinesApi
class SetMainTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testArticles = Resource.Success(Article.articleSamples)

    @MockK
    private lateinit var mockApiService: ApiService

    // SUT
    private lateinit var viewModel: ArticleViewModel

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `runBlockingTest - test fun creating new coroutines`() = runBlockingTest {
        // Given
        viewModel = ArticleViewModel(mockApiService)

        coEvery { mockApiService.getArticles() } coAnswers {
            delay(3000)
            testArticles
        }

        // When
        viewModel.onButtonClicked()

        // Then
        coVerify { mockApiService.getArticles() }

        val articles = viewModel.articles.getValueForTest()
        assertThat(articles).isEqualTo(testArticles)
    }
}
