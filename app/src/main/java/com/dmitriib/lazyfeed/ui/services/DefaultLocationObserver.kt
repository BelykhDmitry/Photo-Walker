package com.dmitriib.lazyfeed.ui.services

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import com.dmitriib.lazyfeed.utils.Logger
import java.util.concurrent.Executor

class DefaultLocationObserver(
    private val executor: Executor,
    private val logger: Logger
) : LocationObserver, LocationListenerCompat {

    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    override fun startObservingLocation(context: Context, listener: LocationListener): Boolean {
        locationManager = context.getSystemService()
        return locationManager?.let { manager ->
            locationListener = listener
            if (ContextCompat.checkSelfPermission(
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
                val isLocationEnabled = LocationManagerCompat.isLocationEnabled(manager)
                logger.d("Location availability check: $isLocationEnabled")
                val provider = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    LocationManager.FUSED_PROVIDER
                } else {
                    LocationManager.GPS_PROVIDER
                }
                val locationRequest = LocationRequestCompat.Builder(10_000L).build()
                LocationManagerCompat.requestLocationUpdates(
                    manager,
                    provider,
                    locationRequest,
                    executor,
                    this
                )
                true
            }
        } ?: false
    }

    override fun stopObservingLocation(context: Context) {
        locationManager?.let {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                LocationManagerCompat.removeUpdates(it, this)
            }
        }
        locationListener = null
    }

    override fun onLocationChanged(location: Location) {
        locationListener?.onLocationChanged(location)
    }
}