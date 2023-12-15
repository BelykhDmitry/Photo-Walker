package com.dmitriib.lazyfeed.data

import android.location.Location
import com.dmitriib.lazyfeed.data.local.LocationItem

class LocationsUtils {

    /**
     * Calculates distance in meters between 2 locations (WGS84).
     * @return distance in meters or null if there is calculation issue.
     */
    fun distanceBetween(first: LocationItem, second: LocationItem): Float? {
        val results = FloatArray(1)
        return runCatching {
            Location.distanceBetween(
                first.lat,
                first.lon,
                second.lat,
                second.lon,
                results
            )
            results.firstOrNull()
        }.getOrDefault(null)
    }
}
