package com.example.sendtoo.di

import com.example.sendtoo.data.network.KtorHttpClient
import com.example.sendtoo.service.FirebaseService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule: Module = module {
    singleOf(::FirebaseService)
    singleOf(::KtorHttpClient)
}