package com.dmitriib.challenge.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val lat: Double,
    val lon: Double,
    val time: Long
)
