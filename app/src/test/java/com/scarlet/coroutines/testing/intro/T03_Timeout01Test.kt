package com.scarlet.coroutines.testing.intro

import com.google.common.truth.Truth.assertThat
import com.scarlet.util.log
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.*
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
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
class T03_Timeout01Test {

    @MockK
    lateinit var userService: UserService

    private val testUser = User("Peter Parker")

    @Before
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `load responds immediately`() = runTest {
        coEvery { userService.load() } returns testUser

        val user = loadUser(userService)

        log("$currentTime")

        assertThat(user).isEqualTo(testUser)
    }

    @Test//(expected = TimeoutCancellationException::class)
    fun `load timed out after 30 seconds`() = runTest {
        coEvery { userService.load() } coAnswers {
            delay(30_000)
            testUser
        }

        loadUser(userService)
        log("$currentTime")
    }

    @Test
    fun `load in less than 30 seconds succeeds`() = runTest {
        coEvery { userService.load() } coAnswers {
            delay(29_999)
            testUser
        }
        val user = loadUser(userService)
        assertThat(user).isEqualTo(testUser)
        log("$currentTime")
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
    fun `testing async in time`() = runTest {
        coEvery { userService.load() } coAnswers {
            log("returning testUser")
            delay(29_999)
            testUser
        }

        val deferred = loadUserAsync(userService)
        // awaiting will advance coroutine
        val user = deferred.await()
        log("$currentTime")

        assertThat(user).isEqualTo(testUser)
    }

    @Test//(expected = TimeoutCancellationException::class)
    fun `testing async timeout`() = runTest {
        coEvery { userService.load() } coAnswers {
            delay(30_000)
            log("returning testUser")
            testUser
        }

        val deferred = loadUserAsync(userService)

//        deferred.await()    // See what happens if you comment out this line
        log("$currentTime")
    }

}