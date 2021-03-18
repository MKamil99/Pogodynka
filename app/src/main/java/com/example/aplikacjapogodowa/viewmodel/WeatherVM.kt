package com.example.aplikacjapogodowa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aplikacjapogodowa.model.SpecificDayForecast
import com.example.aplikacjapogodowa.model.SpecificHourForecast
import com.example.aplikacjapogodowa.model.api.ApiRequest
import com.example.aplikacjapogodowa.model.api.WeatherRepository
import com.example.aplikacjapogodowa.model.responses.CurrentWeatherResponse
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class WeatherVM(application: Application) : AndroidViewModel(application) {
    private val repository : WeatherRepository = WeatherRepository(ApiRequest.getAPI())

    var currentWeather = MutableLiveData<CurrentWeatherResponse>()
    fun setCurrentWeather(cityName : String)
    {
        viewModelScope.launch {
            val response = repository.getCurrentWeather(cityName).awaitResponse()
            if (response.isSuccessful)
            {
                val data = response.body()!!
                currentWeather.value = data
            }
        }
    }

    var currentHourlyForecast = MutableLiveData<List<SpecificHourForecast>>()
    var currentDailyForecast = MutableLiveData<List<SpecificDayForecast>>()
    fun setForecasts(latitude : Double, longitude : Double)
    {
        viewModelScope.launch {
            val response = repository.getForecasts(latitude, longitude).awaitResponse()
            if (response.isSuccessful)
            {
                val data = response.body()!!
                currentHourlyForecast.value = data.hourly
                currentDailyForecast.value = data.daily
            }
        }
    }
}