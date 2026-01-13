package com.example.sendtoo.app

import android.app.Application
import com.example.sendtoo.di.initKoin
import org.koin.android.ext.koin.androidContext

class SendTooApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SendTooApplication)
        }
    }
}