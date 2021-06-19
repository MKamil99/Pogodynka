package com.example.weatherapp.model.db

import androidx.lifecycle.LiveData

class WeatherDataRepository(private val weatherDataDao: WeatherDataDao) {
    val readAll : LiveData<List<WeatherData>> = weatherDataDao.getWholeWeather()
    suspend fun addNewData(weather : WeatherData) = weatherDataDao.insertWeatherData(weather)
    suspend fun deleteStoredData() = weatherDataDao.deleteWeatherData()
}
