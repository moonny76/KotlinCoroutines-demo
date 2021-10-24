package com.org.scarlet.coroutines.basics.myth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigInteger
import java.util.*

// Will this help?
suspend fun findBigPrime_Wish_To_Be_NonBlocking(): BigInteger =
    BigInteger.probablePrime(3072, Random())

suspend fun findBigPrime_ProperWay(): BigInteger = withContext(Dispatchers.Default) {
    BigInteger.probablePrime(3072, Random())
}
