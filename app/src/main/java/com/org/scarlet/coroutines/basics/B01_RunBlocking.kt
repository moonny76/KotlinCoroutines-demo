package com.org.scarlet.coroutines.basics

import com.org.scarlet.util.logi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

object Create_Coroutine_With_RunBlocking_Demo1 {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Hello")

        runBlocking {
            logi(0, "Coroutine created")
            delay(1000)
            logi(0, "Coroutine done")
        }

        println("World")
    }
}

object Create_Coroutine_With_RunBlocking_Demo2 {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println("Hello")

        logi(0, "Coroutine created")
        delay(1000)
        logi(0, "Coroutine done")

        println("World")
    }
}