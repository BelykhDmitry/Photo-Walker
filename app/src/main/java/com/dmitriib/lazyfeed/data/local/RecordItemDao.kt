package com.dmitriib.lazyfeed.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recordItem: RecordItem)

    @Query("SELECT * FROM RecordItem WHERE id = (SELECT MAX(id) FROM RecordItem)")
    suspend fun getLastRecord(): RecordItem

    @Query("SELECT * FROM RecordItem ORDER BY id DESC")
    fun getRecords(): Flow<List<RecordItem>>
}
