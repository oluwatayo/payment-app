package com.example.cashi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform