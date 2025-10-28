package com.example.cashi.app

import android.app.Application
import com.example.cashi.di.initKoin
import org.koin.android.ext.koin.androidContext

class CashiApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@CashiApplication)
        }
    }
}