package com.example.weatherapp.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hour_table")
data class HourData(
    // SpecificHourForecast:
    @PrimaryKey(autoGenerate = true) val id: Int,
    val dt : Long,
    val temp : Double,

    // Weather:
    val description : String,
    val icon : String
)
