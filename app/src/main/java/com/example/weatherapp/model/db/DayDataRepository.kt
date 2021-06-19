package com.example.weatherapp.model.db

import androidx.lifecycle.LiveData

class DayDataRepository(private val dayDataDao: DayDataDao) {
    val readAll : LiveData<List<DayData>> = dayDataDao.getAllDays()
    suspend fun addNewData(day : DayData) = dayDataDao.insertDayData(day)
    suspend fun deleteStoredData() = dayDataDao.deleteDayData()
}
