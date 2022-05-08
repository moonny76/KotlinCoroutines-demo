package com.scarlet.coroutines.advanced

import com.scarlet.util.log
import kotlinx.coroutines.runBlocking
import java.io.IOException

object CvtCallbackToSuspendFun_01 {

    // Callback
    interface AsyncCallback {
        fun onSuccess(result: String)
        fun onError(ex: Exception)
    }

    // Method using callback to simulate a long running task
    fun getData(callback: AsyncCallback, status: Boolean) {
        // Do network request here, and then respond accordingly
        if (status) {
            callback.onSuccess("[Beep.Boop.Beep]")
        } else {
            callback.onError(IOException("Network failure"))
        }
    }

    fun demoCallbackInvocation() {
        getData(object : AsyncCallback {
            override fun onSuccess(result: String) {
                log("Data received: $result")
            }

            override fun onError(ex: Exception) {
                log("Caught ${ex.javaClass.simpleName}")
            }
        }, true)

        getData(object : AsyncCallback {
            override fun onSuccess(result: String) {
                log("Data received: $result")
            }

            override fun onError(ex: Exception) {
                log("Caught ${ex.javaClass.simpleName}")
            }
        }, false)
    }

    // Use resume/resumeWithException
    suspend fun getDataAsync(status: Boolean): String {
        TODO()
    }

    // Use resumeWith only
    suspend fun getDataAsyncV2(status: Boolean): String {
        TODO()
    }

    suspend fun demoAsyncInvocation() {
        try {
            val result = getDataAsync(true)
            log("Data received: $result")
        } catch(ex: Exception) {
            log("Caught ${ex.javaClass.simpleName}")
        }

        try {
            val result = getDataAsync(false)
            log("Data received: $result")
        } catch(ex: Exception) {
            log("Caught ${ex.javaClass.simpleName}")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        demoCallbackInvocation()
//        demoAsyncInvocation()
    }
}
