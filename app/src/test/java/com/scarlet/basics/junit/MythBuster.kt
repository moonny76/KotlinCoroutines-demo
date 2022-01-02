package com.scarlet.basics.junit

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MythBusterTest {
    private val xs: MutableList<String> = arrayListOf()

    @Test
    fun `add test`() {
        // Arrange (Given)

        // Act (When)
        xs.add("Mellow")

        // Assert (Then)
        assertThat(xs).hasSize(1)
    }

    @Test
    fun `remove test`() {
        // Arrange (Given)
        xs.add("Yellow")
        xs.add("Hello")

        // Act (When)
        xs.remove("Yellow")

        // Assert (Then)
        assertThat(xs).hasSize(1)
    }
}