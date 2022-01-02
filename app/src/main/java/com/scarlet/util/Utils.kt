package com.scarlet.util

import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.coroutines.ContinuationInterceptor

val log: Logger = LoggerFactory.getLogger("Coroutines")

fun log(msg: String?) {
    log.info(msg)
}

fun delim(char: String = "-", length: Int = 50) {
    println(char.repeat(length))
}

fun spaces(level: Int) = "\t".repeat(level)

fun CoroutineScope.log(level: Int, msg: String) {
    log("${spaces(level)}$msg")
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

suspend fun CoroutineScope.coroutineDynInfo(indent: Int) {
    delim()
    println("\t".repeat(indent) + "thread = ${Thread.currentThread().name}")
    println("\t".repeat(indent) + "job = ${currentCoroutineContext()[Job]}")
    println("\t".repeat(indent) + "dispatcher = ${currentCoroutineContext()[ContinuationInterceptor]}")
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

fun Job.completeStatus(name: String = "Job", level: Int = 0) = apply {
    println("${spaces(level)}$name: isCancelled = $isCancelled")
}

fun CoroutineScope.completeStatus(name: String = "scope", level: Int = 0) = apply {
    println("${spaces(level)}$name: isCancelled = ${coroutineContext.job.isCancelled}")
}

fun CoroutineScope.onCompletion(name: String = "scope", level: Int = 0) = apply {
    coroutineContext.job.onCompletion(name, level)
}

fun Job.onCompletion(name: String, level: Int = 0): Job = apply {
    invokeOnCompletion {
        println("${spaces(level)}$name: isCancelled = $isCancelled, exception = ${it?.javaClass?.name}")
    }
}

fun <T> Deferred<T>.onCompletion(name: String, level: Int = 0): Deferred<T> = apply {
    invokeOnCompletion {
        println("${spaces(level)}$name: isCancelled = $isCancelled, exception = ${it?.javaClass?.name}")
    }
}
