package com.org.scarlet.coroutines.testing.intro

import com.google.common.truth.Truth.assertThat
import com.org.scarlet.util.AtomicInt
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class T02_VirtualTimeControlTest {

    @Test
    fun `test virtual time control`() = runBlockingTest {
        var count by AtomicInt(0)

        launch {
            delay(1000)
            count = 1
            delay(1000)
            count = 3
            delay(1000)
            count = 5
        }

        assertThat(count).isEqualTo(0)
        println("$currentTime")

//        advanceTimeBy(1000)
//        println("$currentTime")
//
//        assertThat(count).isEqualTo(1)
//
//        advanceTimeBy(1000)
//        println("$currentTime")
//
//        assertThat(count).isEqualTo(3)
//
//        advanceTimeBy(999) // 999
//        println("$currentTime")
//
//        assertThat(count).isEqualTo(5)
    }

    @Test
    fun `paused dispatcher does not execute eagerly`() = runBlockingTest {
        var state by AtomicInt(0)

        pauseDispatcher()
        launch {
            state = 1
            yield()
            state = 2
            delay(1000)
            state = 3
//            delay(1000)
//            state = 4
//            delay(1000)
//            state = 5
        }
        // not started yet
        assertThat(state).isEqualTo(0)

        // Run any tasks that are pending at or before the current virtual clock-time.
        // Calling this function will never advance the clock.
        runCurrent()
        assertThat(state).isEqualTo(2)
        println("$currentTime")

        // Immediately execute all pending tasks and advance the virtual clock-time to the last delay.
        // If new tasks are scheduled due to advancing virtual time, they will be executed before
        // `advanceUntilIdle` returns.
        advanceUntilIdle()
        println("$currentTime")
        assertThat(state).isEqualTo(3)
    }

    @Test
    fun `paused and resume dispatcher`() = runBlockingTest {
        var state by AtomicInt(0)

        launch {
            state = 1
            yield()
            state = 2
            delay(1000)
            state = 3
        }

        assertThat(state).isEqualTo(3)
        println("$currentTime")
    }

    @Test
    fun `paused and resume dispatcher - realistic example`() = runBlockingTest {

        val list = mutableListOf<Int>().apply {
            add(42)
            launch {
                println(Thread.currentThread().name)
                add(777)
            }
        }

        assertThat(list).containsExactly(42)

        assertThat(list).containsExactly(42, 777)
    }
}