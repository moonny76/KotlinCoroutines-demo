package com.org.scarlet.coroutines.basics

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.concurrent.thread

@JvmInline
value class Data(val value: Int)

object UsingSyncCall {

    @JvmStatic
    fun main(args: Array<String>) {

        loadData()

        println("Hello from main")
    }

    private fun loadData() {
        val data = networkRequest()
        println(data)
    }

    // Blocking network request code
    private fun networkRequest(): Data {
        sleep(1_000) // // simulate network request
        return Data(42)
    }

}

object UsingCallback {
    @JvmStatic
    fun main(args: Array<String>) {

        loadData()

        println("Hello from main")
    }

    private fun loadData() {
        networkRequest { data ->
            println(data)
        }
    }

    private fun networkRequest(block: (Data) -> Unit) {
        thread {
            sleep(1_000) // simulate network request
            block(Data(42))
        }
    }
}

object CallbackHell {
    @JvmStatic
    fun main(args: Array<String>) {
        loadData()
    }

    private fun loadData() {
        networkRequest { data ->
            anotherRequest(data) { data1 ->
                anotherRequest(data1) { data2 ->
                    anotherRequest(data2) { data3 ->
                        anotherRequest(data3) { data4 ->
                            anotherRequest(data4) { data5 ->
                                anotherRequest(data5) { data6 ->
                                    anotherRequest(data6) { data7 ->
                                        anotherRequest(data7) { data8 ->
                                            anotherRequest(data8) { data9 ->
                                                anotherRequest(data9) {
                                                    // How many more do you want?
                                                    println(it)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun networkRequest(block: (Data) -> Unit) {
        thread {
            sleep(200) // simulate network request
            block(Data(0))
        }
    }

    private fun anotherRequest(data: Data, block: (Data) -> Unit) {
        thread {
            sleep(200) // simulate network request
            block(Data(data.value + 1))
        }
    }
}

object AsyncWithCoroutine {
    @JvmStatic
    fun main(args: Array<String>) = runBlocking{

        loadData()

        println("Hello from main")
    }

    private suspend fun loadData() {
        // suspend point and resume point
        val data = networkRequest()
        println(data)
    }

    private suspend fun networkRequest(): Data {
        delay(1000)
        return Data(42)
    }

}