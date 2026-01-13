package com.example.sendtoo.data.network

import io.ktor.client.HttpClient

expect class KtorHttpClient{
    fun createHttpClient(): HttpClient
    fun getBaseUrl(): String
}