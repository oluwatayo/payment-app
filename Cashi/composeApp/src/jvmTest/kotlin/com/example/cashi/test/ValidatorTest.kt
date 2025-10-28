package com.example.cashi.test

import com.example.cashi.utils.Validator
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class ValidatorTest {
    private val systemUnderTest: Validator = Validator()

    private val validator = Validator()

    @Test
    fun `isValidEmail returns true for valid email`() {
        assertTrue(validator.isValidEmail("user@example.com"))
        assertTrue(validator.isValidEmail("john.doe+alias@sub.domain.org"))
    }

    @Test
    fun `isValidEmail returns false for invalid or empty email`() {
        assertFalse(validator.isValidEmail(""))
        assertFalse(validator.isValidEmail(null))
        assertFalse(validator.isValidEmail("invalid-email"))
        assertFalse(validator.isValidEmail("user@domain"))
    }

    @Test
    fun `isAmountValid returns true for positive numeric values`() {
        assertTrue(validator.isAmountValid("10"))
        assertTrue(validator.isAmountValid("5.75"))
    }

    @Test
    fun `isAmountValid returns false for invalid or negative values`() {
        assertFalse(validator.isAmountValid("0"))
        assertFalse(validator.isAmountValid("-1"))
        assertFalse(validator.isAmountValid("abc"))
        assertFalse(validator.isAmountValid(""))
    }

    @Test
    fun `isCurrencyValid returns true for non-empty strings`() {
        assertTrue(validator.isCurrencyValid("USD"))
        assertTrue(validator.isCurrencyValid("NGN"))
    }

    @Test
    fun `isCurrencyValid returns false for empty string`() {
        assertFalse(validator.isCurrencyValid(""))
    }
}