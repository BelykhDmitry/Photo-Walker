package com.dmitriib.lazyfeed.ui.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import androidx.core.content.ContextCompat
import com.dmitriib.lazyfeed.domain.Settings
import com.dmitriib.lazyfeed.utils.Logger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener as GmsLocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.util.concurrent.Executor

class GmsLocationObserver(
    private val executor: Executor,
    private val logger: Logger
) : LocationObserver, GmsLocationListener {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationListener: LocationListener? = null

    override fun startObservingLocation(context: Context, listener: LocationListener): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            false
        } else {
            locationListener = listener
            val locationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient = locationClient
            val locationUpdateRequest =
                LocationRequest.Builder(Settings.LOCATION_UPDATE_PERIOD_MILLIS)
                    .build()
            locationClient.locationAvailability.addOnCompleteListener {
                // TODO: add checks for location status before launching service
                logger.d("Location availability check: ${it.result.isLocationAvailable}")
            }
            locationClient.requestLocationUpdates(locationUpdateRequest, executor, this)
            true
        }
    }

    override fun stopObservingLocation(context: Context) {
        fusedLocationClient?.removeLocationUpdates(this)
    }

    override fun onLocationChanged(p0: Location) {
        locationListener?.onLocationChanged(p0)
    }
}