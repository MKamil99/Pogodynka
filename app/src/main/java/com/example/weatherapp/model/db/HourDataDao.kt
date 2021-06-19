package com.example.weatherapp.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HourDataDao {
    @Insert
    suspend fun insertHourData(hour : HourData)

    @Query("DELETE FROM hour_table")
    suspend fun deleteHourData()

    @Query("SELECT * FROM hour_table")
    fun getAllHours() : LiveData<List<HourData>>
}
