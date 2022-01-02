package com.scarlet.livedata

import com.scarlet.model.Article
import com.scarlet.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class LiveDataTest {

    @Test
    fun `articles - explicit Observer`() = runTest {
        // Arrange (Given)

        // Assert (Then)

        // Act (When)
    }

    @Test
    fun `articles - MockK observer`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)
    }

    @Test
    fun `articles - getValueForTest or getOrAwaitValue`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)
    }

    /**/

    @Test
    fun `test topArticle by getValueForTest`() = runTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)
    }

    @Test
    fun `test topArticle by captureValues`() = runTest {
        // Arrange (Given)
        val responses = listOf(
            Resource.Success(Article.articleSamples[0]),
            Resource.Success(Article.articleSamples[1])
        )

        // Act (When)

        // Assert (Then)
    }

    /**/

    @Test
    fun `test articlesByTopAuthor by captureValues`() = runTest {
        // Arrange (Given)
        val testData = listOf(
            Article("A001", "Robert Martin", "Clean Code"),
            Article("A004", "Robert Martin", "Agile Patterns"),
        )

        // Act (When)

        // Assert (Then)
    }

}