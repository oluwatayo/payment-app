package com.example.cashi.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class KtorHttpClient {
    actual fun createHttpClient(): HttpClient = HttpClient(Darwin)
    actual fun getBaseUrl(): String = "http://localhost:3000"
}