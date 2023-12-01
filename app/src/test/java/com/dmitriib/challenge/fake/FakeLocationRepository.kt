package com.dmitriib.challenge.fake

import com.dmitriib.challenge.data.LocationRepository
import com.dmitriib.challenge.data.local.LocationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll

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