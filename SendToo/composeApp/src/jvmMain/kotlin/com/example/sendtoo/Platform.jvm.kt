package com.example.sendtoo


actual fun getPlatform(): Platform = object : Platform{
    override val name: String
        get() = "JVM"
}