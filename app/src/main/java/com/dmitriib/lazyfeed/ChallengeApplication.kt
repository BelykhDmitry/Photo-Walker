package com.dmitriib.lazyfeed

import android.app.Application
import com.dmitriib.lazyfeed.di.AppContainer
import com.dmitriib.lazyfeed.di.DefaultAppContainer

class ChallengeApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
        appContainer.notificationManager.initialize(this)
    }
}
