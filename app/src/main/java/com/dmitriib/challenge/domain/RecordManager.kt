package com.dmitriib.challenge.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface RecordManager {

    fun getRecordStatusFlow(): Flow<RecordState>
    fun createRecord()
    fun startRecord()
    fun pauseRecord()
    fun resumeRecord()
    fun completeRecord()
}

class DefaultRecordManager : RecordManager {

    private val recordStateFlow = MutableStateFlow<RecordState>(RecordState.NoCurrent)

    override fun getRecordStatusFlow(): Flow<RecordState> {
        return recordStateFlow
    }

    override fun createRecord() {
        recordStateFlow.update {
            if (it is RecordState.NoCurrent || it is RecordState.Completed) RecordState.Created(recordId = 1)
            else it
        }
    }

    override fun startRecord() {
        recordStateFlow.update {
            if (it is RecordState.Created) RecordState.Started(it.recordId)
            else it
        }
    }

    override fun pauseRecord() {
        recordStateFlow.update {
            if (it is RecordState.Started) RecordState.Paused(it.recordId)
            else it
        }
    }

    override fun resumeRecord() {
        recordStateFlow.update {
            if (it is RecordState.Paused) RecordState.Started(it.recordId)
            else it
        }
    }

    override fun completeRecord() {
        recordStateFlow.update {
            if (it is RecordState.Paused) RecordState.Completed(it.recordId)
            else it
        }
    }
}

sealed interface RecordState {
    data object NoCurrent : RecordState // По идее быть не должно
    data class Created(val recordId: Int) : RecordState
    data class Started(val recordId: Int) : RecordState
    data class Paused(val recordId: Int) : RecordState
    data class Completed(val recordId: Int) : RecordState
}
