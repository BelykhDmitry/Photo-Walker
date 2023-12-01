package com.dmitriib.challenge.ui.permissions

import android.Manifest
import android.os.Build
import com.dmitriib.challenge.utils.Logger

interface PermissionManager {
    fun getRequiredPermissions(): List<String>
    fun onPermissionResult(result: Map<String, Boolean>): Boolean
}

class LocationServicePermissionManager(private val logger: Logger) : PermissionManager {

    override fun getRequiredPermissions(): List<String> {
        return requiredPermissions
    }

    override fun onPermissionResult(result: Map<String, Boolean>): Boolean {
        logger.d("Permission check result: $result")
        val locationPermissionGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val notificationPermissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                result[Manifest.permission.POST_NOTIFICATIONS] == true
        return locationPermissionGranted && notificationPermissionGranted
    }

    companion object {
        private val requiredPermissions = listOfNotNull(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.POST_NOTIFICATIONS
            } else {
                null
            }
        )
    }
}
