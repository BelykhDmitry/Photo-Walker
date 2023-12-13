package com.dmitriib.challenge.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RecordItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)
