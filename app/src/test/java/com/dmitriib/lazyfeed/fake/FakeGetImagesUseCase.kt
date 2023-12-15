package com.dmitriib.lazyfeed.fake

import com.dmitriib.lazyfeed.domain.GetImagesUseCase
import com.dmitriib.lazyfeed.domain.ImageInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll

class FakeGetImagesUseCase : GetImagesUseCase {
    private val flow = MutableSharedFlow<List<ImageInfo>>()
    override fun invoke(id: Int): Flow<List<ImageInfo>> {
        return flow
    }

    suspend fun emit(value: List<ImageInfo>) {
        flow.emit(value)
    }

    suspend fun emitAll(value: Flow<List<ImageInfo>>) {
        flow.emitAll(value)
    }
}
