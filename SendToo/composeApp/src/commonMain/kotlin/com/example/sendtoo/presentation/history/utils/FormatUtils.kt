package com.example.sendtoo.presentation.history.utils

expect fun formatCurrency(amount: Double, currencyCode: String): String
expect fun formatTimestamp(iso8601: String): String