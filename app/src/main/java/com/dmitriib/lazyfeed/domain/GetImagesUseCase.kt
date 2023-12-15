package com.dmitriib.lazyfeed.domain

import com.dmitriib.lazyfeed.data.ImagesRepository
import com.dmitriib.lazyfeed.data.LocationRepository
import com.dmitriib.lazyfeed.utils.AppDispatchers
import com.dmitriib.lazyfeed.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

interface GetImagesUseCase {
    operator fun invoke(id: Int): Flow<List<ImageInfo>>
}

class GetFlickrImagesUseCase(
    private val locationRepository: LocationRepository,
    private val imagesRepository: ImagesRepository,
    private val dispatchers: AppDispatchers,
    private val logger: Logger
) : GetImagesUseCase {

    override operator fun invoke(id: Int): Flow<List<ImageInfo>> = locationRepository.getLocations(id)
        .map { locations ->
            logger.d("On locations update: ${locations.joinToString()}")
            imagesRepository.loadImages(locations)
                .map {
                    logger.d("On new photo: $it")
                    ImageInfo(IMAGE_URL.format(it.server, it.id, it.secret))
                }
        }
        .flowOn(dispatchers.default)


    companion object {
        // NOTE: Add support of different image sizes depending on screen size
        private const val IMAGE_URL = "https://live.staticflickr.com/%s/%s_%s.jpg"
    }
}
