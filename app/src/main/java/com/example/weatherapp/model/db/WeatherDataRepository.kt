package com.example.weatherapp.model.db

import androidx.lifecycle.LiveData

class WeatherDataRepository(private val weatherDataDao: WeatherDataDao) {
    val readAll : LiveData<WeatherData> = weatherDataDao.getWeatherData()
    suspend fun addNewData(weather : WeatherData) = weatherDataDao.insertWeatherData(weather)
    suspend fun deleteStoredData() = weatherDataDao.deleteWeatherData()
}
