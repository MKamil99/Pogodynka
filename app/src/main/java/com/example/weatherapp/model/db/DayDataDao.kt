package com.example.weatherapp.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DayDataDao {
    @Insert
    suspend fun insertDayData(data : DayData)

    @Query("DELETE FROM day_table")
    suspend fun deleteDayData()

    @Query("SELECT * FROM day_table")
    fun getAllDays() : LiveData<List<DayData>>
}
