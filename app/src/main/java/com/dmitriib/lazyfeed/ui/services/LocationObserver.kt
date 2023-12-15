package com.dmitriib.lazyfeed.ui.services

import android.content.Context
import android.location.LocationListener
import com.dmitriib.lazyfeed.utils.Logger
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.util.concurrent.Executor

interface LocationObserver {

    /**
     * Starts observing location and delivers updates to [listener]
     * @return true if successfully started, false otherwise
     */
    fun startObservingLocation(context: Context, listener: LocationListener): Boolean
    fun stopObservingLocation(context: Context)

    companion object {
        fun createInstance(
            context: Context,
            executor: Executor,
            logger: Logger
        ): LocationObserver {
            val gmsIsAvailable = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
            return if (gmsIsAvailable) {
                GmsLocationObserver(executor, logger)
            } else {
                DefaultLocationObserver(executor, logger)
            }
        }
    }
}
