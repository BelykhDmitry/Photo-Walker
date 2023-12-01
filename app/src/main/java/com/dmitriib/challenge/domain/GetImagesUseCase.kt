package com.dmitriib.challenge.domain

import com.dmitriib.challenge.data.ImagesRepository
import com.dmitriib.challenge.data.LocationRepository
import com.dmitriib.challenge.utils.AppDispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface GetImagesUseCase {
    operator fun invoke(): Flow<List<ImageInfo>>
}

class GetFlickrImagesUseCase(
    private val locationRepository: LocationRepository,
    private val imagesRepository: ImagesRepository,
    private val dispatchers: AppDispatchers
) : GetImagesUseCase {

    override operator fun invoke(): Flow<List<ImageInfo>> = locationRepository.getLocations()
        .map { locations ->
            imagesRepository.loadImages(locations)
                .map { ImageInfo(IMAGE_URL.format(it.server, it.id, it.secret)) }
        }
        .flowOn(dispatchers.default)


    companion object {
        // NOTE: Add support of different image sizes depending on screen size
        private const val IMAGE_URL = "https://live.staticflickr.com/%s/%s_%s.jpg"
    }
}
