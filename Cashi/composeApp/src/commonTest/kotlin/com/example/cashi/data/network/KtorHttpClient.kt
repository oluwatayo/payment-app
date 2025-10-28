package com.example.cashi.data.network

import io.ktor.client.HttpClient

expect class KtorHttpClient{
    fun createHttpClient(): HttpClient
    fun getBaseUrl(): String
}