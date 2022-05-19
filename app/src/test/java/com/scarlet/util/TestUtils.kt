package com.scarlet.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlin.coroutines.ContinuationInterceptor

@ExperimentalCoroutinesApi
val CoroutineScope.testDispatcher get() = coroutineContext[ContinuationInterceptor] as TestDispatcher