package com.scarlet.coroutines.testing.intro

import com.google.common.truth.Truth.assertThat
import com.scarlet.util.log
import com.scarlet.util.onCompletion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
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
                log("loop is running -- $count")
            }
            log("all done")
        }
    }
}

@ExperimentalCoroutinesApi
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CoroutineLeakTest {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val subject = Subject()

    @Before
    fun before() {
        log("before my leaky test")
    }

    @After
    fun after() {
//        scope.cancel()  // One way to solve leaking problem
        log("after my leaky test")
    }

    // runBlocking, runBlockingTest, no test dispatcher all the same
    @Test
    fun `create a leak`() = runTest {

        with(subject) {
            scope.loop()
        }

        assertThat(subject.someBoolean).isTrue()
        log("my leaky test has completed")

    }

    @Test
    fun `create another test`() {

        log("some other tests would run now")

        runBlocking { delay(5_000) } //  This mimics execution of other tests while a leak is happening.
    }


    /**/

    @Test
    fun runBlocking_Demo() = runBlocking {
        log(coroutineContext.toString())
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        log(scope.coroutineContext.toString())
        log("1")

        launch(Dispatchers.Default) {
            log(coroutineContext.toString())
            log("2")
            log("before delay")
            delay(3000)
            log("after delay")
        }

        log("3")
    }

    @Test
    fun runTest_Demo() = runTest {
        log(coroutineContext.toString())
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        log(scope.coroutineContext.toString())
        log("1")

        launch(Dispatchers.Default) {
            log(coroutineContext.toString())
            log("2")
            log("before delay")
            delay(3000)
            log("after delay")
        }

        log("3")
    }

    @Test
    fun runBlockingTest_Demo() = runBlockingTest {
        log(coroutineContext.toString())
        val scope = CoroutineScope(Job() + Dispatchers.IO)
        log(scope.coroutineContext.toString())
        log("1")

        launch(Dispatchers.Default) {
            log(coroutineContext.toString())
            log("2")
            log("before delay")
            delay(3000)
            log("after delay")
        }

        log("3")
    }

    @Test
    fun runBlocking_Demo1() = runBlocking {
        launch {
            log("parent in")
            launch {
                log("child before delay")
                delay(5000)
                log("child after delay")
            }.onCompletion("child")
            log("parent out")
        }.onCompletion("parent")

        log("done")
    }

    @Test
    fun runBlockingTest_Demo1() = runBlockingTest {
        launch {
            log("parent in")
            launch {
                log("child before delay")
                delay(5000)
                log("child after delay")
            }.onCompletion("child")
            log("parent out")
        }.onCompletion("parent")

        log("done")
    }

    @Test
    fun runTest_Demo1() = runTest {
        launch {
            log("parent in")
            launch {
                log("child before delay")
                delay(5000)
                log("child after delay")
            }.onCompletion("child")
            log("parent out")
        }.onCompletion("parent")

        log("done")
    }

    @Test
    fun runBlocking_demo2() = runBlocking {
        val scope = CoroutineScope(Job())
        scope.launch {
            log("parent in")
            launch {
                log("child before delay")
                delay(5000)
                log("child after delay")
            }.onCompletion("child")
            log("parent out")
        }.onCompletion("parent")

        log("done")
    }

    @Test
    fun runBlockingTest_demo2() = runBlockingTest {
        val scope = CoroutineScope(Job())
        scope.launch {
            log("parent in")
            launch {
                log("child before delay")
                delay(5000)
                log("child after delay")
            }.onCompletion("child")
            log("parent out")
        }.onCompletion("parent")

        log("done")
    }

    @Test
    fun runTest_demo2() = runTest {
        val scope = CoroutineScope(Job())
        scope.launch {
            log("parent in")
            launch {
                log("child before delay")
                delay(5000)
                log("child after delay")
            }.onCompletion("child")
            log("parent out")
        }.onCompletion("parent")

        log("done")
    }

    @Test
    fun coroutineScope_Demo1() = runTest { // change this to runBlockingTest (This job has not completed yet)
        val scope = CoroutineScope(Job())
        log("start")

        coroutineScope {
            launch(Dispatchers.Default) {
                log("child before delay")
                delay(5000)
                log("child after delay")
            }.onCompletion("child")
        }

        log("done")
    }


    @Test
    fun coroutineScope_Demo2() = runTest {
        val scope = CoroutineScope(Job())

        coroutineScope {
            scope.launch {
                log("child before delay")
                delay(5000)
                log("child after delay")
            }.onCompletion("parent")
        }

        log("done")
    }

}