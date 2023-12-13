package com.dmitriib.challenge.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LocationItem::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun locationItemDao(): LocationItemDao

    companion object {
        private const val DATABASE_NAME = "locations_db"

        @Volatile
        private var Instance: LocationDatabase? = null

        fun getDatabase(context: Context): LocationDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LocationDatabase::class.java, DATABASE_NAME)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
