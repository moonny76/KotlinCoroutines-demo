package com.scarlet.junit.rules

import org.junit.rules.TestWatcher
import org.junit.runner.Description

class YourTestRule : TestWatcher() {
    override fun starting(description: Description) {
        println("your rule before...")
    }

    override fun finished(description: Description) {
        println("your rule after...")
    }
}
