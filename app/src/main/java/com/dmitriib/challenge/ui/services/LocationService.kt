package com.dmitriib.challenge.ui.services

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.dmitriib.challenge.ChallengeApplication
import com.dmitriib.challenge.data.local.LocationItem
import com.dmitriib.challenge.domain.AddNewLocationUseCase
import com.dmitriib.challenge.utils.Logger

class LocationService : Service() {

    private val addNewLocationUseCase: AddNewLocationUseCase by lazy {
        (applicationContext as ChallengeApplication).appContainer.addNewLocationUseCase
    }
    private val logger: Logger by lazy {
        (applicationContext as ChallengeApplication).appContainer.logger
    }
    private val locationObserver: LocationObserver by lazy {
        (applicationContext as ChallengeApplication).appContainer.locationObserver
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestLocationUpdates()
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        locationObserver.stopObservingLocation(this)
        super.onDestroy()
    }

    private fun startForeground() {
        val notificationManager = (applicationContext as ChallengeApplication)
            .appContainer
            .notificationManager
        val notification = notificationManager.createNotification(this)
        ServiceCompat.startForeground(this, SERVICE_ID, notification, notificationType)
    }

    private fun requestLocationUpdates() {
        if (!locationObserver.startObservingLocation(this, ::onLocationChanged)) {
            stopSelf()
        }
    }

    private fun onLocationChanged(location: Location) {
        logger.d("On new location: $location")
        addNewLocationUseCase(
            LocationItem(
                lat = location.latitude,
                lon = location.longitude,
                time = location.time
            )
        )
    }

    companion object {
        private const val SERVICE_ID = 100
        private val notificationType: Int
            get() = if (Build.VERSION.SDK_INT >= VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            } else {
                0
            }
    }
}
