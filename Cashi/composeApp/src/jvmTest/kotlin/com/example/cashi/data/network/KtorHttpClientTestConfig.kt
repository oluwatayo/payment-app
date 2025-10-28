package com.example.cashi.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders

object KtorHttpClientTestConfig {
    var handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = { _ ->
        respond(
            content = "{}",
            headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        )
    }

    var baseUrl: String = "http://test" // Not used by MockEngine, but PaymentService reads it
}

actual class KtorHttpClient {
    actual fun createHttpClient(): HttpClient =
        HttpClient(
            MockEngine { request ->
                KtorHttpClientTestConfig.handler(this, request)
            }
        ) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true; explicitNulls = false })
            }
            install(HttpTimeout) { requestTimeoutMillis = 15_000 }
            expectSuccess = true
        }

    actual fun getBaseUrl(): String = KtorHttpClientTestConfig.baseUrl
}