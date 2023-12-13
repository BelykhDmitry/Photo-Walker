package com.dmitriib.challenge.domain

import com.dmitriib.challenge.data.local.RecordItem
import com.dmitriib.challenge.data.local.RecordItemDao
import com.dmitriib.challenge.utils.AppDispatchers
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception

interface RecordManager {

    fun getRecordStatusFlow(): Flow<RecordState>
    fun createRecord()
    fun startRecord()
    fun pauseRecord()
    fun resumeRecord()
    fun completeRecord()
}

class DefaultRecordManager(
    private val getImagesUseCase: GetImagesUseCase,
    private val recordItemDao: RecordItemDao,
    private val appDispatchers: AppDispatchers,
    private val logger: Logger
) : RecordManager {

    private val recordStateFlow = MutableStateFlow<RecordState>(RecordState.NoCurrent())

    override fun getRecordStatusFlow(): Flow<RecordState> {
        return recordStateFlow.combine(observeImageInfo()) { state, images ->
            logger.d("RecordManager:onCombine: $state | $images")
            state
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeImageInfo(): Flow<Unit> {
        return recordStateFlow.map {
            it.recordId
        }.distinctUntilChanged()
            .flatMapConcat {
                getImagesUseCase(it)
                    .transform { images ->
                        recordStateFlow.update { state ->
                            when (state) {
                                is RecordState.NoCurrent,
                                is RecordState.Created -> state
                                is RecordState.Completed -> state.copy(images = images)
                                is RecordState.Paused -> state.copy(images = images)
                                is RecordState.Started -> state.copy(images = images)
                            }
                        }
                        emit(Unit)
                    }
                    .distinctUntilChanged()
            }

    }

    override fun createRecord() {
        CoroutineScope(appDispatchers.single).launch {
            val currentState = recordStateFlow.value
            if (currentState is RecordState.NoCurrent || currentState is RecordState.Completed) {
                try {
                    recordItemDao.insert(RecordItem())
                    val lastRecord = recordItemDao.getLastRecord()
                    recordStateFlow.update {
                        RecordState.Created(
                            recordId = lastRecord.id,
                            images = emptyList()
                        )
                    }
                } catch (e: Exception) {
                    logger.d("Error creating Record", e)
                }
            }
        }
    }

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
}

sealed interface RecordState {

    val recordId: Int
    data class NoCurrent(override val recordId: Int = -1) : RecordState
    data class Created(override val recordId: Int, val images: List<ImageInfo>) : RecordState
    data class Started(override val recordId: Int, val images: List<ImageInfo>) : RecordState
    data class Paused(override val recordId: Int, val images: List<ImageInfo>) : RecordState
    data class Completed(override val recordId: Int, val images: List<ImageInfo>) : RecordState
}
