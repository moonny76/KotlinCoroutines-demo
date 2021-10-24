package com.org.scarlet.coroutines.testing.version1

import com.google.common.truth.Truth.assertThat
import com.org.scarlet.model.Article.Companion.articleSamples
import com.org.scarlet.util.Resource
import com.org.scarlet.util.getValueForTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ArticleViewModelTest {
    
    // SUT
    private lateinit var viewModel: ArticleViewModel

    // sample test data
    private val testArticles = Resource.Success(articleSamples)

    @Before
    fun init() {
        TODO()
    }

    // More on livedata testing later ...
    @Test
    fun `loadData - test suspend fun not creating new coroutines`() = runBlockingTest {
        // Given

        // When
        viewModel.loadData()

        // Then
    }

    @Test
    fun `onButtonClicked - test fun creating new coroutines - runBlocking`() = runBlocking {
        // Given

        // When
        viewModel.onButtonClicked()

        delay(3000)

        // Then
        val articles = viewModel.articles.getValueForTest()
        assertThat(articles).isEqualTo(testArticles)
    }

    @Test
    fun `onButtonClicked - test fun creating new coroutines - runBlockingTest`() = runBlockingTest {
        // Given

        // When
        viewModel.onButtonClicked()

        delay(10_000) // Will this help?

        // Then
        val articles = viewModel.articles.getValueForTest()
        assertThat(articles).isEqualTo(testArticles)
    }
}