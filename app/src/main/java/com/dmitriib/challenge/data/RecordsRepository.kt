package com.dmitriib.challenge.data

import com.dmitriib.challenge.data.local.RecordItem
import com.dmitriib.challenge.data.local.RecordItemDao
import com.dmitriib.challenge.utils.AppDispatchers
import com.dmitriib.challenge.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception

interface RecordsRepository {

    suspend fun createRecordAsync(): RecordItem?

    fun getRecordsFlow(): Flow<List<RecordItem>>
}

class DefaultRecordsRepository(
    private val recordItemDao: RecordItemDao,
    private val appDispatchers: AppDispatchers,
    private val logger: Logger
) : RecordsRepository {

    override suspend fun createRecordAsync(): RecordItem? {
        return try {
            recordItemDao.insert(RecordItem())
            recordItemDao.getLastRecord()
        } catch (e: Exception) {
            logger.d("Error creating Record", e)
            null
        }
    }

    override fun getRecordsFlow(): Flow<List<RecordItem>> {
        return recordItemDao.getRecords()
            .flowOn(appDispatchers.io)
    }
}
