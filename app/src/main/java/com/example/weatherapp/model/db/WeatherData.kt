package com.example.weatherapp.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "main_table")
data class WeatherData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val cityName : String,
    val dt : Long,

    // Weather:
    val description : String,
    val icon : String,

    // Coordination:
    val lon : Double,
    val lat : Double,

    // Details:
    val feels_like : Double,
    val temp : Double,
    val pressure : Int,
    val humidity: Int,

    // SunTimes:
    val sunrise : Long,
    val sunset : Long
)
