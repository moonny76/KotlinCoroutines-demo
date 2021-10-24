package com.org.scarlet.basics.matchers

import com.org.scarlet.basics.matchers.IsOnlyDigits.Companion.onlyDigits
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class CustomMatchersTest {
    @Test
    fun givenAString_whenIsOnlyDigits_thenCorrect() {
        val digits = "1234"
        assertThat(digits, onlyDigits())
    }

    @Test
    fun givenAString_whenNotIsOnlyDigits_thenIncorrect() {
        val digits = "abc1234"
        assertThat(digits, onlyDigits())
    }

    @Test
    fun givenAnEvenInteger_whenDivisibleByTwo_thenCorrect() {
        val ten = 10
        val two = 2

//        assertThat(ten, is(divisibleBy(two)));
    }

    @Test
    fun givenAnOddInteger_whenNotDivisibleByTwo_thenCorrect() {
        val eleven = 11
        val two = 2

//        assertThat(eleven, is(not(divisibleBy(two))));
    }
}
