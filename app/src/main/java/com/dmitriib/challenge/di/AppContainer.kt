package com.dmitriib.challenge.di

import android.content.Context
import android.content.pm.PackageManager
import com.dmitriib.challenge.data.DefaultImagesRepository
import com.dmitriib.challenge.data.DefaultLocationRepository
import com.dmitriib.challenge.data.ImagesRepository
import com.dmitriib.challenge.data.LocationRepository
import com.dmitriib.challenge.data.LocationsUtils
import com.dmitriib.challenge.data.NetworkSettings
import com.dmitriib.challenge.data.local.LocationDatabase
import com.dmitriib.challenge.data.network.FlickrApiService
import com.dmitriib.challenge.data.network.RetrofitHelper
import com.dmitriib.challenge.domain.AddNewLocationUseCase
import com.dmitriib.challenge.domain.DefaultRecordManager
import com.dmitriib.challenge.domain.GetFlickrImagesUseCase
import com.dmitriib.challenge.domain.GetImagesUseCase
import com.dmitriib.challenge.domain.RecordManager
import com.dmitriib.challenge.ui.notifications.ChallengeNotificationManager
import com.dmitriib.challenge.ui.permissions.LocationServicePermissionManager
import com.dmitriib.challenge.ui.permissions.PermissionManager
import com.dmitriib.challenge.ui.services.LocationObserver
import com.dmitriib.challenge.utils.AppDispatchers
import com.dmitriib.challenge.utils.ConsoleLogger
import com.dmitriib.challenge.utils.Logger
import com.dmitriib.dmitrii_belykh_challenge.R
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
    val recordManager: RecordManager
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
        GetFlickrImagesUseCase(locationRepository, imagesRepository, dispatchers)
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
    override val recordManager: RecordManager = DefaultRecordManager()
}
