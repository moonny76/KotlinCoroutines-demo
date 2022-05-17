package com.scarlet.coroutines.basics.myth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.util.*

// Will this help?
suspend fun findBigPrime_Wish_To_Be_NonBlocking(): BigInteger =
    BigInteger.probablePrime(2048, Random())

suspend fun findBigPrime_ProperWay(): BigInteger = withContext(Dispatchers.Default) {
    BigInteger.probablePrime(2048, Random())
}
