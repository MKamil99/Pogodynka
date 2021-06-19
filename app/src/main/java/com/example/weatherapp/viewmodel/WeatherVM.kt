package com.example.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.weatherapp.model.SpecificDayForecast
import com.example.weatherapp.model.SpecificHourForecast
import com.example.weatherapp.model.api.ApiRequest
import com.example.weatherapp.model.api.WeatherRepository
import com.example.weatherapp.model.responses.CurrentWeatherResponse
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.util.*

// ViewModel which contains data about weather:
class WeatherVM(application: Application) : DatabaseVM(application) {
    private val repository : WeatherRepository = WeatherRepository(ApiRequest.getAPI())
    private val language = if (Locale.getDefault().displayLanguage == "polski") "pl" else "en"

    // Current weather:
    private val mutCurrentWeather = MutableLiveData<CurrentWeatherResponse>()
    val currentWeather : LiveData<CurrentWeatherResponse> get() = mutCurrentWeather
    fun setCurrentWeatherByName(cityName : String?) {
        if (!cityName.isNullOrEmpty()) {
            viewModelScope.launch {
                val response = repository.getCurrentWeather(cityName, language).awaitResponse()
                mutCityExists.value = response.isSuccessful

                val data = response.body()
                if (response.isSuccessful && data != null) {
                    // Save data for displaying:
                    mutCurrentWeather.value = data

                    // Save data for further sessions:
                    saveWeatherInfo(data)
                }
            }
        }
    }
    fun setCurrentWeatherByCoordination(latitude : Double?, longitude : Double?) {
        if (latitude != null && longitude != null) {
            viewModelScope.launch {
                val response = repository.getCurrentWeatherByCoordination(latitude, longitude, language).awaitResponse()
                mutCityExists.value = response.isSuccessful

                val data = response.body()
                if (response.isSuccessful && data != null) {
                    // Save data for displaying:
                    mutCurrentWeather.value = data

                    // Save data for further sessions:
                    saveWeatherInfo(data)
                }
            }
        }
    }

    // Boolean which informs if city with given name exists or not:
    private val mutCityExists = MutableLiveData<Boolean>()
    val cityExists : LiveData<Boolean> get() = mutCityExists
    fun setCityExists(value : Boolean) {
        mutCityExists.value = value
    }

    // Forecasts (hourly for 24 hours and daily for 7 days):
    private val mutCurrentHourlyForecast = MutableLiveData<List<SpecificHourForecast>>()
    private val mutCurrentDailyForecast = MutableLiveData<List<SpecificDayForecast>>()
    val currentHourlyForecast : LiveData<List<SpecificHourForecast>> get() = mutCurrentHourlyForecast
    val currentDailyForecast : LiveData<List<SpecificDayForecast>> get() = mutCurrentDailyForecast
    fun setForecasts(latitude : Double?, longitude : Double?) {
        if (latitude != null && longitude != null) {
            viewModelScope.launch {
                val response = repository.getForecasts(latitude, longitude, language).awaitResponse()

                val data = response.body()
                if (response.isSuccessful && data != null) {
                    // Save data for displaying:
                    mutCurrentHourlyForecast.value = data.hourly.subList(1, 25)
                    mutCurrentDailyForecast.value = data.daily.subList(0, 7)

                    // Save data for further sessions:
                    saveForecasts(data)
                }
            }
        }
    }
}
