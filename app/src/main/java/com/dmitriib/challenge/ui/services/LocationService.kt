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
import com.dmitriib.challenge.domain.RecordManager
import com.dmitriib.challenge.domain.RecordState
import com.dmitriib.challenge.ui.notifications.ChallengeNotificationManager
import com.dmitriib.challenge.ui.notifications.NotificationUserAction
import com.dmitriib.challenge.utils.AppDispatchers
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LocationService : Service() {

    private var started = false
    private var lastActions = emptyList<NotificationUserAction>()

    private val addNewLocationUseCase: AddNewLocationUseCase by lazy {
        (applicationContext as ChallengeApplication).appContainer.addNewLocationUseCase
    }
    private val logger: Logger by lazy {
        (applicationContext as ChallengeApplication).appContainer.logger
    }
    private val locationObserver: LocationObserver by lazy {
        (applicationContext as ChallengeApplication).appContainer.locationObserver
    }
    private val recordManager: RecordManager by lazy {
        (applicationContext as ChallengeApplication).appContainer.recordManager
    }
    private val appDispatchers: AppDispatchers by lazy {
        (applicationContext as ChallengeApplication).appContainer.dispatchers
    }
    private val scope by lazy {  CoroutineScope(appDispatchers.main) }
    private val notificationManager: ChallengeNotificationManager by lazy {
        (applicationContext as ChallengeApplication)
            .appContainer
            .notificationManager
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!started) {
            scope.launch {
                recordManager.getRecordStatusFlow().collect { state ->
                    when (state) {
                        is RecordState.Completed -> {
                            locationObserver.stopObservingLocation(this@LocationService)
                            stopSelf()
                        }
                        is RecordState.Created -> {
                            updateNotification(listOf(NotificationUserAction.START))
                        }
                        is RecordState.Paused -> {
                            locationObserver.stopObservingLocation(this@LocationService)
                            updateNotification(listOf(
                                NotificationUserAction.STOP,
                                NotificationUserAction.RESUME
                            ))
                        }
                        is RecordState.Started -> {
                            requestLocationUpdates()
                            updateNotification(listOf(
                                NotificationUserAction.PAUSE
                            ))
                        }
                        else -> Unit
                    }
                }
            }
            started = true
        }
        startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        locationObserver.stopObservingLocation(this)
        started = false
        scope.cancel()
        super.onDestroy()
    }

    private fun startForeground() {
        val notification = notificationManager.createNotification(this, lastActions)
        ServiceCompat.startForeground(this, SERVICE_ID, notification, notificationType)
    }

    private fun updateNotification(actions: List<NotificationUserAction>) {
        lastActions = actions
        notificationManager.updateNotification(
            this,
            actions,
            SERVICE_ID
        )
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
