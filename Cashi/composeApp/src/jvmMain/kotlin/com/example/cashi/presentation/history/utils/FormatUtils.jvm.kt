package com.example.cashi.presentation.history.utils

import java.util.Locale

actual fun formatTimestamp(iso8601: String): String {
    return iso8601
}

actual fun formatCurrency(amount: Double, currencyCode: String): String {
    val currencySymbol = when (currencyCode) {
        "USD" -> "$"
        else -> "€"
    }
    return "$currencySymbol${"%,.2f".format(Locale.getDefault(), amount)}"
}