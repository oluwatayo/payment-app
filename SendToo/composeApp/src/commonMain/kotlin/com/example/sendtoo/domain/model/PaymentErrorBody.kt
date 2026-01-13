package com.example.sendtoo.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentErrorBody(
    val success: Boolean?,
    val error: String?,
    val code: String?
)