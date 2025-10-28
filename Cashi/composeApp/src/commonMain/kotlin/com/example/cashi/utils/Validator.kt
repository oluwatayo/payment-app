package com.example.cashi.utils


class Validator {
    fun isValidEmail(email: CharSequence?): Boolean =
        !email.isNullOrEmpty() && emailRegex.matches(email.trim())

    fun isAmountValid(amount: String): Boolean =
        amount.toDoubleOrNull() != null && amount.toDouble() > 0.0

    fun isCurrencyValid(currency: String): Boolean = currency.isNotEmpty()

    private val emailRegex =
        Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

}