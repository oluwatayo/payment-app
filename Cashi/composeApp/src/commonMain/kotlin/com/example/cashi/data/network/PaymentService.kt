package com.example.cashi.data.network

import com.example.cashi.domain.error.InsufficientFunds
import com.example.cashi.domain.error.InvalidAmount
import com.example.cashi.domain.error.NetworkError
import com.example.cashi.domain.error.PaymentError
import com.example.cashi.domain.error.RecipientNotFound
import com.example.cashi.domain.error.UnknownPaymentError
import com.example.cashi.domain.error.UnsupportedCurrency
import com.example.cashi.domain.error.defaultErrorMessage
import com.example.cashi.domain.model.PaymentErrorBody
import com.example.cashi.domain.model.PaymentResponse
import com.example.cashi.domain.model.TransactionItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.io.IOException

class PaymentService(ktorHttpClient: KtorHttpClient) {
    private val client: HttpClient = ktorHttpClient.createHttpClient()
    private val baseUrl = ktorHttpClient.getBaseUrl()
    suspend fun makePayment(transaction: TransactionItem): Result<PaymentResponse> =
        try {
            val paymentResponse: PaymentResponse = client.post("$baseUrl/payments") {
                contentType(ContentType.Application.Json)
                setBody(transaction)
            }.body()
            Result.success(paymentResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.message)
            when (e) {
                is ClientRequestException -> {
                    val errorBody = e.response.body<PaymentErrorBody?>()
                    Result.failure(handleErrorBody(error = errorBody))
                }

                is IOException -> {
                    Result.failure(NetworkError())
                }

                else -> {
                    Result.failure(UnknownPaymentError())
                }
            }
        }

    private fun handleErrorBody(error: PaymentErrorBody?): PaymentError = when (error?.code) {
        "INSUFFICIENT_FUNDS" -> InsufficientFunds(message = error.error ?: defaultErrorMessage)
        "INVALID_EMAIL" -> RecipientNotFound(message = error.error ?: defaultErrorMessage)
        "INVALID_AMOUNT" -> InvalidAmount(message = error.error ?: defaultErrorMessage)
        "INVALID_CURRENCY" -> UnsupportedCurrency(message = error.error ?: defaultErrorMessage)
        else -> UnknownPaymentError(message = error?.error ?: defaultErrorMessage)
    }
}

