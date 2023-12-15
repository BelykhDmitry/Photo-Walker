package com.dmitriib.lazyfeed.di

import android.content.Context
import android.content.pm.PackageManager
import com.dmitriib.lazyfeed.data.DefaultImagesRepository
import com.dmitriib.lazyfeed.data.DefaultLocationRepository
import com.dmitriib.lazyfeed.data.ImagesRepository
import com.dmitriib.lazyfeed.data.LocationRepository
import com.dmitriib.lazyfeed.data.LocationsUtils
import com.dmitriib.lazyfeed.data.NetworkSettings
import com.dmitriib.lazyfeed.data.local.LocationDatabase
import com.dmitriib.lazyfeed.data.network.FlickrApiService
import com.dmitriib.lazyfeed.data.network.RetrofitHelper
import com.dmitriib.lazyfeed.domain.AddNewLocationUseCase
import com.dmitriib.lazyfeed.data.DefaultRecordsRepository
import com.dmitriib.lazyfeed.domain.GetFlickrImagesUseCase
import com.dmitriib.lazyfeed.domain.GetImagesUseCase
import com.dmitriib.lazyfeed.domain.RecordManagerFactory
import com.dmitriib.lazyfeed.data.RecordsRepository
import com.dmitriib.lazyfeed.ui.notifications.ChallengeNotificationManager
import com.dmitriib.lazyfeed.ui.permissions.LocationServicePermissionManager
import com.dmitriib.lazyfeed.ui.permissions.PermissionManager
import com.dmitriib.lazyfeed.ui.services.LocationObserver
import com.dmitriib.lazyfeed.utils.AppDispatchers
import com.dmitriib.lazyfeed.utils.ConsoleLogger
import com.dmitriib.lazyfeed.utils.Logger
import com.dmitriib.lazyfeed.R
import java.util.concurrent.Executors

/**
 * Simple DI container, temporary fast solution.
 * NOTE: Migrate to DI framework
 */
interface AppContainer {
    val logger: Logger
    val dispatchers: AppDispatchers
    val permissionManager: PermissionManager
    val notificationManager: ChallengeNotificationManager
    val locationDatabase: LocationDatabase
    val locationsUtils: LocationsUtils
    val locationRepository: LocationRepository
    val flickrApiService: FlickrApiService
    val imagesRepository: ImagesRepository
    val addNewLocationUseCase: AddNewLocationUseCase
    val getImagesUseCase: GetImagesUseCase
    val locationObserver: LocationObserver
    val networkSettings: NetworkSettings
    val recordsRepository: RecordsRepository
    val recordManagerFactory: RecordManagerFactory
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val logger: Logger by lazy { ConsoleLogger() }
    override val dispatchers: AppDispatchers = AppDispatchers()
    override val permissionManager: PermissionManager by lazy {
        LocationServicePermissionManager(logger)
    }
    override val notificationManager: ChallengeNotificationManager by lazy {
        ChallengeNotificationManager()
    }
    override val locationDatabase: LocationDatabase by lazy {
        LocationDatabase.getDatabase(context)
    }
    override val locationsUtils: LocationsUtils = LocationsUtils()
    override val locationRepository: LocationRepository by lazy {
        DefaultLocationRepository(locationDatabase.locationItemDao(), dispatchers, logger)
    }
    override val flickrApiService: FlickrApiService by lazy {
        val retrofit = RetrofitHelper.createRetrofit(FlickrApiService.BASE_URL)
        retrofit.create(FlickrApiService::class.java)
    }
    override val imagesRepository: ImagesRepository by lazy {
        DefaultImagesRepository(flickrApiService, dispatchers, logger, networkSettings)
    }
    override val addNewLocationUseCase: AddNewLocationUseCase by lazy {
        AddNewLocationUseCase(locationRepository, locationsUtils)
    }
    override val getImagesUseCase: GetImagesUseCase by lazy {
        GetFlickrImagesUseCase(locationRepository, imagesRepository, dispatchers, logger)
    }
    override val locationObserver: LocationObserver by lazy {
        LocationObserver.createInstance(context, Executors.newSingleThreadExecutor(), logger)
    }
    override val networkSettings: NetworkSettings by lazy {
        val key = context.getString(R.string.flickr_key)
        val bundle = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        ).metaData
        val apiKey = bundle?.getString(key) ?: ""
        NetworkSettings(apiKey)
    }
    override val recordsRepository: RecordsRepository = DefaultRecordsRepository(locationDatabase.recordItemDao(), dispatchers, logger)
    override val recordManagerFactory: RecordManagerFactory = RecordManagerFactory(getImagesUseCase, dispatchers, logger)
}
