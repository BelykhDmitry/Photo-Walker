package com.dmitriib.lazyfeed.domain

import com.dmitriib.lazyfeed.data.LocationRepository
import com.dmitriib.lazyfeed.data.LocationsUtils
import com.dmitriib.lazyfeed.data.local.LocationItem

class AddNewLocationUseCase(
    private val locationRepository: LocationRepository,
    private val locationsUtils: LocationsUtils
) {
    private var currentLocation: LocationItem? = null

    // NOTE: ensure distance calculation and search radius with location accuracy
    operator fun invoke(newLocation: LocationItem) {
        val localLocation = currentLocation
        if (localLocation != null && localLocation.recordId == newLocation.recordId) {
            val distance = locationsUtils.distanceBetween(localLocation, newLocation)
            if (distance != null && distance >= Settings.DISTANCE_BETWEEN_POINTS_METERS) {
                currentLocation = newLocation
                locationRepository.addNewLocation(newLocation)
            }
        } else {
            currentLocation = newLocation
            locationRepository.addNewLocation(newLocation)
        }
    }
}
