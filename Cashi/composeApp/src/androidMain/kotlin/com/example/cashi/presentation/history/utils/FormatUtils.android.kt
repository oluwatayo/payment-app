package com.example.cashi.presentation.history.utils

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

actual fun formatCurrency(amount: Double, currencyCode: String): String {
    return runCatching {
        NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance(currencyCode.uppercase(Locale.getDefault()))
            maximumFractionDigits = 2
            minimumFractionDigits = if (currency?.defaultFractionDigits() == 0) 0 else 2
        }.format(amount)
    }.getOrElse {
        // Fallback: show raw amount with code
        "${"%,.2f".format(Locale.getDefault(), amount)} $currencyCode"
    }
}

private fun Currency.defaultFractionDigits(): Int = this.defaultFractionDigits

actual fun formatTimestamp(iso8601: String): String {
    return runCatching {
        val instant = Instant.parse(iso8601)
        val zdt = instant.atZone(ZoneId.systemDefault())
        DateTimeFormatter.ofPattern("MMM d, yyyy • HH:mm").format(zdt)
    }.getOrElse { iso8601 }
}