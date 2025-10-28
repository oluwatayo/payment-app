package com.example.cashi.di

import com.example.cashi.data.network.KtorHttpClient
import com.example.cashi.service.FirebaseService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::FirebaseService)
    singleOf(::KtorHttpClient)
}