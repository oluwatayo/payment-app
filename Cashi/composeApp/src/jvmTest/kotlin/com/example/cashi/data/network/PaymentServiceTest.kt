package com.example.cashi.data.network

import com.example.cashi.domain.error.InsufficientFunds
import com.example.cashi.domain.error.InvalidAmount
import com.example.cashi.domain.error.NetworkError
import com.example.cashi.domain.error.RecipientNotFound
import com.example.cashi.domain.error.UnknownPaymentError
import com.example.cashi.domain.error.UnsupportedCurrency
import com.example.cashi.domain.model.PaymentResponse
import com.example.cashi.domain.model.TransactionItem
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpResponseData

class PaymentServiceTest {
    private fun json(content: String) = content.trimIndent()
    private val jsonHeader = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

    private fun setHandler(
        block: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
    ) {
        KtorHttpClientTestConfig.handler = block
        KtorHttpClientTestConfig.baseUrl = "http://localhost:3000"
    }

    private fun newService(): PaymentService = PaymentService(KtorHttpClient())

    @Test
    fun `parses 201 Created into PaymentResponse`() = runTest {
        setHandler {
            respond(
                content = json(
                    """
                    {
                      "success": true,
                      "message": "Payment processed successfully",
                      "payment": {
                        "id": 123,
                        "transactionId": "txn_001",
                        "recipientEmail": "alice@example.com",
                        "amount": 12.5,
                        "currency": "USD",
                        "status": "SUCCESS",
                        "timestamp": "2025-10-28T10:00:00Z"
                      }
                    }
                    """
                ),
                status = HttpStatusCode.Created,
                headers = jsonHeader
            )
        }

        val service = newService()
        val result = service.makePayment(
            TransactionItem(
                recipientEmail = "alice@example.com",
                amount = 12.5,
                currency = "USD",
            )
        )

        assertTrue(result.isSuccess)
        val body: PaymentResponse = result.getOrThrow()
        val payment = requireNotNull(body.payment)
        assertTrue(body.success)
        assertEquals("Payment processed successfully", body.message)
        assertEquals(123, payment.id)
        assertEquals("SUCCESS", payment.status)
    }

    @Test
    fun `maps INSUFFICIENT_FUNDS error code`() = runTest {
        setHandler {
            respond(
                content = json(
                    """
                    {
                      "success": false,
                      "error": "Balance too low",
                      "code": "INSUFFICIENT_FUNDS"
                    }
                    """
                ),
                status = HttpStatusCode.BadRequest,
                headers = jsonHeader
            )
        }

        val service = newService()
        val result = service.makePayment(
            TransactionItem(recipientEmail = "a@b.com", amount = 999.0, currency = "USD")
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is InsufficientFunds)
        assertEquals("Balance too low", (result.exceptionOrNull() as InsufficientFunds).message)
    }

    @Test
    fun `maps INVALID_EMAIL to RecipientNotFound`() = runTest {
        setHandler {
            respond(
                content = json(
                    """
                    {
                      "success": false,
                      "error": "Recipient not found",
                      "code": "INVALID_EMAIL"
                    }
                    """
                ),
                status = HttpStatusCode.BadRequest,
                headers = jsonHeader
            )
        }

        val result = newService().makePayment(
            TransactionItem(recipientEmail = "missing@example.com", amount = 10.0, currency = "USD")
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RecipientNotFound)
    }

    @Test
    fun `maps INVALID_AMOUNT and INVALID_CURRENCY`() = runTest {
        setHandler {
            respond(
                content = json(
                    """{ "success": false, "error": "Amount must be a positive number", "code": "INVALID_AMOUNT" }"""
                ),
                status = HttpStatusCode.BadRequest,
                headers = jsonHeader
            )
        }
        var res = newService().makePayment(
            TransactionItem(recipientEmail = "a@b.com", amount = -1.0, currency = "USD")
        )
        assertTrue(res.isFailure)
        assertTrue(res.exceptionOrNull() is InvalidAmount)

        setHandler {
            respond(
                content = json(
                    """{ "success": false, "error": "Unsupported currency", "code": "INVALID_CURRENCY" }"""
                ),
                status = HttpStatusCode.BadRequest,
                headers = jsonHeader
            )
        }
        res = newService().makePayment(
            TransactionItem(recipientEmail = "a@b.com", amount = 1.0, currency = "ZZZ")
        )
        assertTrue(res.isFailure)
        assertTrue(res.exceptionOrNull() is UnsupportedCurrency)
    }

    @Test
    fun `unknown error code maps to UnknownPaymentError`() = runTest {
        setHandler {
            respond(
                content = json(
                    """{ "success": false, "error": "New error type", "code": "SOMETHING_NEW" }"""
                ),
                status = HttpStatusCode.BadRequest,
                headers = jsonHeader
            )
        }

        val result = newService().makePayment(
            TransactionItem(recipientEmail = "a@b.com", amount = 1.0, currency = "USD")
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is UnknownPaymentError)
    }

    @Test
    fun `network exceptions map to NetworkError`() = runTest {
        setHandler { throw java.io.IOException("network error") }

        val result = newService().makePayment(
            TransactionItem(recipientEmail = "a@b.com", amount = 1.0, currency = "USD")
        )
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NetworkError)
    }
}