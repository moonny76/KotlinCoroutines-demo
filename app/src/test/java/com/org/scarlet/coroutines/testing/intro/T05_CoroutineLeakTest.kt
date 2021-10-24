package com.org.scarlet.coroutines.testing.intro

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

class Subject {
    var someBoolean = false

    fun CoroutineScope.loop() {
        someBoolean = true

        launch {
            repeat(10) { count ->
                delay(timeMillis = 1_000)
                println("loop is running -- $count")
            }
            println("all done")
        }
    }
}

@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CoroutineLeakTest {

    val scope = CoroutineScope(Dispatchers.Default)

    val subject = Subject()

    @Before
    fun before() {
        println("before my leaky test")
    }

    @After
    fun after() {
//        scope.cancel()  // One way to solve leaking problem
        println("after my leaky test")
    }

    // runBlocking, runBlockingTest, no test dispatcher all the same
    @Test
    fun `create a leak`() = runBlockingTest{

        with(subject) {
            scope.loop()
        }

        assertThat(subject.someBoolean).isTrue()
        println("my leaky test has completed")
    }

    @Test
    fun `create another test`() {

        println("some other tests would run now")

        runBlocking { delay(11_000) } //  This mimics execution of other tests while a leak is happening.
    }
}