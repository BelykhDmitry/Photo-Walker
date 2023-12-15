package com.dmitriib.lazyfeed.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponse(
    @SerialName("photos") val photos: PhotosData,
    @SerialName("stat") val stat: String? = null
)

@Serializable
data class PhotosData(
    @SerialName("photo") val photoList: List<Photo>
)

@Serializable
data class Photo(
    @SerialName("id") val id: String,
    @SerialName("secret") val secret: String,
    @SerialName("server") val server: String,
)
