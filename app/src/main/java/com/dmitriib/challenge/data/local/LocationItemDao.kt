package com.dmitriib.challenge.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: LocationItem)

    @Query("SELECT * FROM LocationItem ORDER BY id DESC")
    fun getItems(): Flow<List<LocationItem>>

    @Query("SELECT * FROM LocationItem WHERE recordId = :recordId ORDER BY id DESC")
    fun getItems(recordId: Int): Flow<List<LocationItem>>

    @Query("DELETE FROM LocationItem")
    fun clearItems()
}
