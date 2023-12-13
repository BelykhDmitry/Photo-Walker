package com.dmitriib.challenge.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecordItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(recordItem: RecordItem)

    @Query("SELECT * FROM RecordItem WHERE id = (SELECT MAX(id) FROM RecordItem)")
    suspend fun getLastRecord(): RecordItem
}
