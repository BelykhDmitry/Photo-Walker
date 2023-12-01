package com.dmitriib.challenge.domain

import com.dmitriib.challenge.data.LocationRepository
import com.dmitriib.challenge.data.LocationsUtils
import com.dmitriib.challenge.data.local.LocationItem

class AddNewLocationUseCase(
    private val locationRepository: LocationRepository,
    private val locationsUtils: LocationsUtils
) {
    private var currentLocation: LocationItem? = null

    // NOTE: ensure distance calculation and search radius with location accuracy
    operator fun invoke(newLocation: LocationItem) {
        val localLocation = currentLocation
        if (localLocation != null) {
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
