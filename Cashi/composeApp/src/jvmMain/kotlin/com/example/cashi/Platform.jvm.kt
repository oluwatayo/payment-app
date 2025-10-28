package com.example.cashi


actual fun getPlatform(): Platform = object : Platform{
    override val name: String
        get() = "JVM"
}