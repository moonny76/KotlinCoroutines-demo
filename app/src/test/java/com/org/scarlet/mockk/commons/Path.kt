package com.org.scarlet.mockk.commons

interface Path {
    fun fileName(): Path
    fun readText(): CharSequence
    fun writeText(text: CharSequence): Unit

    suspend fun readAsync(): String
    suspend fun writeAsync(text: CharSequence): Unit
    suspend fun doAsyncWork(): Unit
}