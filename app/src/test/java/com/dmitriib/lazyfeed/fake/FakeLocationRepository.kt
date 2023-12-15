package com.dmitriib.lazyfeed.fake

import com.dmitriib.lazyfeed.data.LocationRepository
import com.dmitriib.lazyfeed.data.local.LocationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeLocationRepository : LocationRepository {

    private val flow = MutableSharedFlow<List<LocationItem>>()

    suspend fun emit(value: List<LocationItem>) {
        flow.emit(value)
    }

    override fun addNewLocation(newLocation: LocationItem) = Unit

    override fun getLocations(): Flow<List<LocationItem>> {
        return flow
    }
}