package com.example.weatherapp.model.db

import androidx.lifecycle.LiveData

class HourDataRepository(private val hourDataDao: HourDataDao) {
    val readAll : LiveData<List<HourData>> = hourDataDao.getAllHours()
    suspend fun addNewData(hour : HourData) = hourDataDao.insertHourData(hour)
    suspend fun deleteStoredData() = hourDataDao.deleteHourData()
}
