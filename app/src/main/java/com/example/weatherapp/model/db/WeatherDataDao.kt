package com.example.weatherapp.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDataDao {
    @Insert
    suspend fun insertWeatherData(weather : WeatherData)

    @Query("DELETE FROM main_table")
    suspend fun deleteWeatherData()

    @Query("SELECT * FROM main_table LIMIT 1")
    fun getWeatherData() : LiveData<WeatherData>
}
