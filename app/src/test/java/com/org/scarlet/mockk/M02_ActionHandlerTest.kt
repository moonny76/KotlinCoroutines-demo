package com.org.scarlet.mockk

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class M02_ActionHandlerTest {
    // SUT
    lateinit var actionHandler: ActionHandler

    @Before
    fun init() {

    }

    @Test
    fun `getValue - should return valid string if doRequest succeed`() {
        // Given

        // When

        // Then
        assertThat(actionHandler.value).isEqualTo("data")
    }

    @Test
    fun `getValue - should return null if doRequest fail`() {
        // Given

        // When
        actionHandler.doRequest("failed query");

        // Then
        assertThat(actionHandler.value).isNull()
    }

    @Test
    fun `getValue - should return valid string if doRequest succeeds - via argument captor, capture when stubbing`() {
        // Given

        // When

        // Then
        assertThat(actionHandler.value).isEqualTo("data")
    }

    @Test
    fun `getValue - should return valid string if doRequest succeeds - via argument captor, capture when verify`() {
        // Given

        // When

        // Then
        assertThat(actionHandler.value).isEqualTo("data")
    }
}