package com.example.cashi.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionItem(
    val amount: Double,
    val recipientEmail: String,
    val currency: String
)

@Serializable
data class Payment(
    val id: Long,
    val transactionId: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val status: String,
    val timestamp: String
) {
    constructor() : this(
        id = 0,
        transactionId = "",
        recipientEmail = "",
        amount = 0.0,
        currency = "",
        status = "",
        timestamp = ""
    )
}

@Serializable
data class PaymentResponse(
    val success: Boolean,
    val payment: Payment? = null,
    val message: String? = null
)