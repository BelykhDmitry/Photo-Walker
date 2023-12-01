package com.dmitriib.challenge.fake

import com.dmitriib.challenge.data.ImagesRepository
import com.dmitriib.challenge.data.local.LocationItem
import com.dmitriib.challenge.data.network.Photo

class FakeImagesRepository() : ImagesRepository {

    private var answer: List<Photo> = emptyList()

    fun setAnswer(list: List<Photo>) {
        answer = list
    }

    override suspend fun loadImages(list: List<LocationItem>): List<Photo> {
        return answer
    }
}
