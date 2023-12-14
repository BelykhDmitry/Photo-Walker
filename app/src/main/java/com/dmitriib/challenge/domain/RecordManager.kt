package com.dmitriib.challenge.domain

import com.dmitriib.challenge.utils.AppDispatchers
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update

interface RecordManager {
    fun startRecord()
    fun pauseRecord()
    fun resumeRecord()
    fun completeRecord()

    fun getRecordStatusFlow(): Flow<RecordState>
}

class RecordManagerImpl(
    val recordId: Int,
    private val getImagesUseCase: GetImagesUseCase,
    private val appDispatchers: AppDispatchers,
    private val logger: Logger
): RecordManager {

    private val recordStateFlow = MutableStateFlow<RecordState>(RecordState.Created(recordId, emptyList()))

    override fun startRecord() {
        recordStateFlow.update {
            if (it is RecordState.Created) RecordState.Started(it.recordId, emptyList())
            else it
        }
    }

    override fun pauseRecord() {
        recordStateFlow.update {
            if (it is RecordState.Started) RecordState.Paused(it.recordId, it.images)
            else it
        }
    }

    override fun resumeRecord() {
        recordStateFlow.update {
            if (it is RecordState.Paused) RecordState.Started(it.recordId, it.images)
            else it
        }
    }

    override fun completeRecord() {
        recordStateFlow.update {
            if (it is RecordState.Paused) RecordState.Completed(it.recordId, it.images)
            else it
        }
    }

    override fun getRecordStatusFlow(): Flow<RecordState> {
        return recordStateFlow.combine(observeImageInfo()) { state, _ ->
            logger.d("RecordManager:onCombine: $state")
            state
        }.flowOn(appDispatchers.single)
    }

    private fun observeImageInfo(): Flow<Unit> {
        return getImagesUseCase(recordId)
            .transform { images ->
                recordStateFlow.update { state ->
                    when (state) {
                        is RecordState.Created -> state
                        is RecordState.Completed -> state.copy(images = images)
                        is RecordState.Paused -> state.copy(images = images)
                        is RecordState.Started -> state.copy(images = images)
                    }
                }
                emit(Unit)
            }.distinctUntilChanged()
    }
}

class RecordManagerFactory(
    private val getImagesUseCase: GetImagesUseCase,
    private val appDispatchers: AppDispatchers,
    private val logger: Logger
) {
    // Need memory usage optimization. Add size limit, remove oldest?
    private val managers: MutableMap<Int, RecordManager> = mutableMapOf()

    fun create(id: Int): RecordManager {
        return managers[id] ?: RecordManagerImpl(id, getImagesUseCase, appDispatchers, logger).also {
            managers[id] = it
        }
    }
}

sealed interface RecordState {

    val recordId: Int
    val images: List<ImageInfo>
    data class Created(override val recordId: Int, override val images: List<ImageInfo>) :
        RecordState
    data class Started(override val recordId: Int, override val images: List<ImageInfo>) :
        RecordState
    data class Paused(override val recordId: Int, override val images: List<ImageInfo>) :
        RecordState
    data class Completed(override val recordId: Int, override val images: List<ImageInfo>) :
        RecordState
}
