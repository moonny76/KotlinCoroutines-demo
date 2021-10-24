package com.org.scarlet.mockk

import com.org.scarlet.mockk.commons.Path
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class M01_Mockk_CoroutinesTest {

    /**
     * Coroutines:
     * MockK provides functions prefixed with `co` as equivalents to other functions,
     * such as `coEvery` and `coAnswers`.
     * - coEvery
     * - coJustRun
     * - coVerify
     * - coVerifyAll
     * - coVerifyOrder
     * - coVerifySequence
     * - coExcludeRecords
     * - coMatch
     * - coMatchNullable
     * - coWithArg
     * - coWithNullableArg
     * - coAnswers
     * - coAndThen
     * - coInvoke
     */
    @Test
    fun `MockK - coroutines`() = runBlocking {
        val mockedPath = mockk<Path>()

        coEvery { mockedPath.readAsync() } returns "hello world"
//        coEvery { mockedPath.readAsync() } coAnswers { "hello world" }

        coEvery { mockedPath.doAsyncWork() } coAnswers { println("Async done") }

        coEvery { mockedPath.writeAsync(any()) } coAnswers {
            mockedPath.doAsyncWork()
        }

        println(mockedPath.readAsync())
        mockedPath.writeAsync("Hello")
    }

    /**
     * Coroutine - verify
     */
    @Test
    fun `MockK - coroutine verify`() = runBlockingTest {
        val mockedPath = mockk<Path>(relaxed = true)

        mockedPath.readAsync()

        coVerify { mockedPath.readAsync() }
    }

}