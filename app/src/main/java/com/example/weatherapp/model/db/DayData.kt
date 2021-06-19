package com.example.weatherapp.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_table")
data class DayData(
    // SpecificDayForecast:
    @PrimaryKey(autoGenerate = true) val id: Int,
    val dt : Long,

    // Temperatures:
    val max : Double,
    val min : Double,

    // Weather:
    val description : String,
    val icon : String
)
