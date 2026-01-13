package com.example.sendtoo.data.network

import io.ktor.client.HttpClient

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KtorHttpClient {
    fun createHttpClient(): HttpClient
    fun getBaseUrl(): String
}