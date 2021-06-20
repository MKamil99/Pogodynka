package com.example.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.db.*
import com.example.weatherapp.model.responses.CurrentWeatherResponse
import com.example.weatherapp.model.responses.OneCallResponse
import kotlinx.coroutines.launch

// View Model which adds database methods to all classes that inherits from it:
abstract class DatabaseVM(application: Application) : LocationVM(application) {
    private val database = WeatherDatabase.getDatabase(application)

    //                                      MAIN INFO
    private val weatherRepository : WeatherDataRepository = WeatherDataRepository(database.weatherDataDao())
    val weatherInfo = weatherRepository.readAll
    protected fun saveWeatherInfo(data : CurrentWeatherResponse) {
        viewModelScope.launch {
            // Delete old records:
            weatherRepository.deleteStoredData()

            // Add new record:
            weatherRepository.addNewData(
                WeatherData(id = 0, cityName = data.name, dt = data.dt, description = data.weather[0].description,
                    icon = data.weather[0].icon, lon = data.coord.lon, lat = data.coord.lat,
                    feels_like = data.main.feels_like, temp = data.main.temp, pressure = data.main.pressure,
                    humidity = data.main.humidity, sunrise = data.sys.sunrise, sunset = data.sys.sunset)
            )
        }
    }

    //                                 HOURLY AND DAILY FORECASTS
    private val hourRepository : HourDataRepository = HourDataRepository(database.hourDataDao())
    private val dayRepository : DayDataRepository = DayDataRepository(database.dayDataDao())
    protected fun saveForecasts(data : OneCallResponse) {
        viewModelScope.launch {
            // Delete old records:
            hourRepository.deleteStoredData()
            dayRepository.deleteStoredData()

            // Add new records from Hourly Forecast:
            val hours = data.hourly.subList(1, 25)
            for (hour in hours) {
                hourRepository.addNewData(
                    HourData(id = 0, dt = hour.dt, temp = hour.temp,
                        description = hour.weather[0].description, icon = hour.weather[0].icon)
                )
            }

            // Add new records from Daily Forecast:
            val days = data.daily.subList(0, 7)
            for (day in days) {
                dayRepository.addNewData(
                    DayData(id = 0, dt = day.dt, max = day.temp.max, min = day.temp.min,
                        description = day.weather[0].description, icon = day.weather[0].icon)
                )
            }
        }
    }
}
