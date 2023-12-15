package com.dmitriib.lazyfeed.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApiService {

    // NOTE: research "tags" query param to make response better
    @GET("rest")
    suspend fun getImageForLocation(
        @Query("method") method: String = METHOD,
        @Query("api_key") apiKey: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("radius") radius: Double,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int,
        @Query("format") format: String = JSON_TYPE,
        @Query("nojsoncallback") jsonType: Int = JSON_TYPE_CODE
    ) : PhotosResponse

    companion object {
        const val BASE_URL = "https://www.flickr.com/services/"
        private const val METHOD = "flickr.photos.search"
        private const val JSON_TYPE_CODE = 1
        private const val JSON_TYPE = "json"
    }
}
