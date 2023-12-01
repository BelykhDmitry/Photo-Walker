package com.dmitriib.challenge.data

import com.dmitriib.challenge.data.local.LocationItem
import com.dmitriib.challenge.data.local.LocationItemDao
import com.dmitriib.challenge.utils.AppDispatchers
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

interface LocationRepository {
    fun addNewLocation(newLocation: LocationItem)
    fun getLocations(): Flow<List<LocationItem>>
}

class DefaultLocationRepository(
    private val locationItemDao: LocationItemDao,
    private val dispatchers: AppDispatchers,
    private val logger: Logger
) : LocationRepository {

    // NOTE: Workaround at this moment - clear data every App start.
    // Will be removed with support of multiple routes.
    init {
        CoroutineScope(dispatchers.single).launch {
            try {
                locationItemDao.clearItems()
            } catch (t: Throwable) {
                logger.d("Error clearing table", t)
            }
        }
    }

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
}
