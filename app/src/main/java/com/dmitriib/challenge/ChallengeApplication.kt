package com.dmitriib.challenge

import android.app.Application
import com.dmitriib.challenge.di.AppContainer
import com.dmitriib.challenge.di.DefaultAppContainer

class ChallengeApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = DefaultAppContainer(this)
        appContainer.notificationManager.initialize(this)
    }
}
