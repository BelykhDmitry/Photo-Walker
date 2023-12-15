package com.dmitriib.lazyfeed.data

import com.dmitriib.lazyfeed.data.local.LocationItem
import com.dmitriib.lazyfeed.data.local.LocationItemDao
import com.dmitriib.lazyfeed.utils.AppDispatchers
import com.dmitriib.lazyfeed.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

interface LocationRepository {
    fun addNewLocation(newLocation: LocationItem)
    fun getLocations(): Flow<List<LocationItem>>
    fun getLocations(recordId: Int): Flow<List<LocationItem>>
}

class DefaultLocationRepository(
    private val locationItemDao: LocationItemDao,
    private val dispatchers: AppDispatchers,
    private val logger: Logger
) : LocationRepository {

    override fun addNewLocation(newLocation: LocationItem) {
        CoroutineScope(dispatchers.single).launch {
            try {
                locationItemDao.insert(newLocation)
            } catch (t: Throwable) {
                logger.d("Error while inserting new location", t)
            }
        }
    }

    override fun getLocations(): Flow<List<LocationItem>> {
        return locationItemDao
            .getItems()
            .flowOn(dispatchers.io)
    }

    override fun getLocations(recordId: Int): Flow<List<LocationItem>> {
        return locationItemDao
            .getItems(recordId)
            .flowOn(dispatchers.io)
    }
}
