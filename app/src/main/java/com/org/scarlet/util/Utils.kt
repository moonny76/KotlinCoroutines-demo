package com.org.scarlet.util

import kotlinx.coroutines.*
import java.math.BigInteger
import kotlin.coroutines.ContinuationInterceptor
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun delim(char: String = "-", length: Int = 50) {
    println(char.repeat(length))
}

fun spaces(level: Int) = "\t".repeat(level)

fun logi(level: Int, msg: String) {
    println("${spaces(level)}${Thread.currentThread().name}: $msg")
}

fun CoroutineScope.log(level: Int, msg: String) {
    println("${spaces(level)}${Thread.currentThread().name}: $msg")
    println("${spaces(level)}$coroutineContext")
}

fun CoroutineScope.coroutineInfo(indent: Int) {
    delim()
    println("\t".repeat(indent) + "thread = ${Thread.currentThread().name}")
    println("\t".repeat(indent) + "job = ${coroutineContext[Job]}")
    println("\t".repeat(indent) + "dispatcher = ${coroutineContext[ContinuationInterceptor]}")
    println("\t".repeat(indent) + "name = ${coroutineContext[CoroutineName]}")
    println("\t".repeat(indent) + "handler = ${coroutineContext[CoroutineExceptionHandler]}")
    delim()
}

fun scopeInfo(scope: CoroutineScope, indent: Int) {
    delim()
    println("\t".repeat(indent) + "Scope's job = ${scope.coroutineContext[Job]}")
    println("\t".repeat(indent) + "Scope's dispatcher = ${scope.coroutineContext[ContinuationInterceptor]}")
    println("\t".repeat(indent) + "Scope's name = ${scope.coroutineContext[CoroutineName]}")
    println("\t".repeat(indent) + "Scope's handler = ${scope.coroutineContext[CoroutineExceptionHandler]}")
    delim()
}

fun AtomicInt(initial: Int = 0) = object: ReadWriteProperty<Any?, Int> {
    private val internal = java.util.concurrent.atomic.AtomicInteger(initial)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Int = internal.get()

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        internal.set(value)
    }
}

suspend fun fibonacci(n: BigInteger): BigInteger =
    withContext(Dispatchers.Default) {
        log(0, "inside fibonacci")
        fib(n)
    }

private fun fib(n: BigInteger): BigInteger =
    if (n <= 1.toBigInteger()) 1.toBigInteger()
    else fib(n - 1.toBigInteger()) + fib(n - 2.toBigInteger())