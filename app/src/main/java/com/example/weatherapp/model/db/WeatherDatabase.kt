package com.example.weatherapp.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DayData::class, HourData::class, WeatherData::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun dayDataDao() : DayDataDao
    abstract fun hourDataDao() : HourDataDao
    abstract fun weatherDataDao() : WeatherDataDao

    companion object{
        @Volatile
        private var INSTANCE : WeatherDatabase? = null
        fun getDatabase(context: Context) : WeatherDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            else synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
