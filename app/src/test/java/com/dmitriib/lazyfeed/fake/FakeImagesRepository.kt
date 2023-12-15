package com.dmitriib.lazyfeed.fake

import com.dmitriib.lazyfeed.data.ImagesRepository
import com.dmitriib.lazyfeed.data.local.LocationItem
import com.dmitriib.lazyfeed.data.network.Photo

class FakeImagesRepository() : ImagesRepository {

    private var answer: List<Photo> = emptyList()

    fun setAnswer(list: List<Photo>) {
        answer = list
    }

    override suspend fun loadImages(list: List<LocationItem>): List<Photo> {
        return answer
    }
}
