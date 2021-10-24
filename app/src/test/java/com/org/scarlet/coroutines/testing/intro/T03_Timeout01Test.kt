package com.org.scarlet.coroutines.testing.intro

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

data class User(val name: String)

interface UserService {
    suspend fun load(): User
}

suspend fun loadUser(userService: UserService): User =
    withTimeout(30_000) {
        userService.load()
    }

@ExperimentalCoroutinesApi
class B03_TimeoutTest {

    @MockK
    lateinit var userService: UserService

    private val testUser = User("Peter Parker")

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `load responds immediately`() = runBlockingTest {
        coEvery { userService.load() } returns testUser

        val user = loadUser(userService)

        println(currentTime)
        assertThat(user).isEqualTo(testUser)
    }

    @Test(expected = TimeoutCancellationException::class)
    fun `load timed out after 30 seconds`() = runBlockingTest {
        coEvery { userService.load() } coAnswers {
            delay(30_000)
            testUser
        }

        loadUser(userService)
        println(currentTime)
    }

    @Test
    fun `load in less than 30 seconds succeeds`() = runBlockingTest {
        coEvery { userService.load() } coAnswers {
            delay(29_999)
            testUser
        }
        val user = loadUser(userService)
        assertThat(user).isEqualTo(testUser)
    }

    /**
     * Testing async case
     */

    private fun CoroutineScope.loadUserAsync(userService: UserService): Deferred<User> = async {
        withTimeout(30_000) {
            userService.load()
        }
    }

    @Test
    fun `testing async in time`() = runBlockingTest {
        coEvery { userService.load() } coAnswers {
            delay(29_999)
            testUser
        }

        val deferred = loadUserAsync(userService)
        // waiting will advance coroutine
        val user = deferred.await()
        println(currentTime)

        assertThat(user).isEqualTo(testUser)
    }

    @Test(expected = TimeoutCancellationException::class)
    fun `testing async timeout`() = runBlockingTest {
        coEvery { userService.load() } coAnswers {
            delay(30_000)
            testUser
        }

        val deferred = loadUserAsync(userService)

        deferred.await()    // See what happens if you comment out this line
    }

}