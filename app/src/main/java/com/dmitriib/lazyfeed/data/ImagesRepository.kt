package com.dmitriib.lazyfeed.data

import com.dmitriib.lazyfeed.data.local.LocationItem
import com.dmitriib.lazyfeed.data.network.FlickrApiService
import com.dmitriib.lazyfeed.data.network.Photo
import com.dmitriib.lazyfeed.domain.Settings.DEFAULT_SEARCH_RADIUS_KM
import com.dmitriib.lazyfeed.utils.AppDispatchers
import com.dmitriib.lazyfeed.utils.Logger
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

interface ImagesRepository {
    suspend fun loadImages(list: List<LocationItem>): List<Photo>
}

class DefaultImagesRepository(
    private val flickrApiService: FlickrApiService,
    private val appDispatchers: AppDispatchers,
    private val logger: Logger,
    private val networkSettings: NetworkSettings,
    private val coroutineContext: CoroutineContext = appDispatchers.io + SupervisorJob(),
) : ImagesRepository {

    // NOTE: not the best solution for this case. Need to find better one
    private val photoByLocationCache = ConcurrentHashMap<LocationItem, Photo>()

    // NOTE: Add work with retry policy
    // NOTE: Add server error codes handling
    override suspend fun loadImages(list: List<LocationItem>): List<Photo> =
        withContext(coroutineContext) {
            list.map { location ->
                location to async {
                    photoByLocationCache[location] ?: flickrApiService.getImageForLocation(
                        apiKey = networkSettings.flickrApiKey,
                        lat = location.lat,
                        lon = location.lon,
                        radius = DEFAULT_SEARCH_RADIUS_KM,
                        perPage = DEFAULT_PAGE_SIZE,
                        page = DEFAULT_PAGE
                    ).photos.photoList.firstOrNull()
                }
            }.mapNotNull { (location, deferred) ->
                try {
                    deferred.await()?.also {
                        photoByLocationCache[location] = it
                    }
                } catch (t: Throwable) {
                    logger.d("Error loading image", t)
                    null
                }
            }
        }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 1
        private const val DEFAULT_PAGE = 1
    }
}
