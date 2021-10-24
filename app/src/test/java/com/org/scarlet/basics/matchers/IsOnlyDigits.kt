package com.org.scarlet.basics.matchers

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.lang.NumberFormatException

class IsOnlyDigits : TypeSafeMatcher<String>() {
    /**
     * matchesSafely(T t): contains our matching logic
     */
    override fun matchesSafely(item: String): Boolean {
        return try {
            item.toInt()
            true
        } catch (nfe: NumberFormatException) {
            false
        }
    }

    /**
     * customizes the message the client will get when our matching logic is not fulfilled
     */
    override fun describeTo(description: Description) {

        // attaching a text that represents our expectations
        description.appendText("only digits")
    }

    companion object {
        /**
         * public API
         */
        fun onlyDigits(): Matcher<String> = IsOnlyDigits()
    }
}
