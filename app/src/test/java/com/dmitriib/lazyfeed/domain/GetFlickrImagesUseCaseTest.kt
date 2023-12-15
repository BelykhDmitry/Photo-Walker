package com.dmitriib.lazyfeed.domain

import com.dmitriib.lazyfeed.MainDispatcherRule
import com.dmitriib.lazyfeed.data.local.LocationItem
import com.dmitriib.lazyfeed.data.network.Photo
import com.dmitriib.lazyfeed.fake.FakeImagesRepository
import com.dmitriib.lazyfeed.fake.FakeLocationRepository
import com.dmitriib.lazyfeed.utils.AppDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetFlickrImagesUseCaseTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var locationRepository: FakeLocationRepository
    private lateinit var imagesRepository: FakeImagesRepository
    private lateinit var getImagesUseCase: GetFlickrImagesUseCase

    @Before
    fun setUp() {
        locationRepository = FakeLocationRepository()
        imagesRepository = FakeImagesRepository()
        getImagesUseCase = GetFlickrImagesUseCase(
            locationRepository, imagesRepository, AppDispatchers()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun observingLocations_whenNewLocation_thenFormatUrl() = runTest {
        val testLocation = LocationItem(lat = .0, lon = .0, time = 0L)
        val testPhoto = Photo(TEST_ID, TEST_SECRET, TEST_SERVER)
        imagesRepository.setAnswer(listOf(testPhoto))

        val images = mutableListOf<List<ImageInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getImagesUseCase().toList(images)
        }

        locationRepository.emit(listOf(testLocation))
        advanceUntilIdle()
        assertEquals(1, images.size)
        assertEquals(TEST_URL, images.first().first().imageUrl)
    }

    companion object {
        private const val TEST_ID = "test_id"
        private const val TEST_SECRET = "test_secret"
        private const val TEST_SERVER = "test_server"
        private const val TEST_URL =
            "https://live.staticflickr.com/${TEST_SERVER}/${TEST_ID}_${TEST_SECRET}.jpg"
    }
}