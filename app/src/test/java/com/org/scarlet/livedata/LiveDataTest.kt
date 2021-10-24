package com.org.scarlet.livedata

import com.org.scarlet.model.Article
import com.org.scarlet.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class LiveDataTest {

    @Test
    fun `articles - explicit Observer`() = runBlockingTest {
        // Arrange (Given)

        // Assert (Then)

        // Act (When)
    }

    @Test
    fun `articles - MockK observer`() = runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)
    }

    @Test
    fun `articles - getValueForTest`() = runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)
    }

    @Test
    fun `articles - getOrAwaitValue`() = runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)
    }

    /**/

    @Test
    fun `test topArticles by getValueForTest`() = runBlockingTest {
        // Arrange (Given)

        // Act (When)

        // Assert (Then)
    }

    @Test
    fun `test topArticles by captureValues`() = runBlockingTest {
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
    fun `test articlesByTopAuthor by captureValues`() = runBlockingTest {
        // Arrange (Given)
        val testData = listOf(
            Article("A001", "Robert Martin", "Clean Code"),
            Article("A004", "Robert Martin", "Agile Patterns"),
        )

        // Act (When)

        // Assert (Then)
    }

}