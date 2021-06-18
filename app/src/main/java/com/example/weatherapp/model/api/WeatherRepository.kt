package com.example.weatherapp.model.api

import com.example.weatherapp.model.responses.CurrentWeatherResponse
import com.example.weatherapp.model.responses.OneCallResponse
import retrofit2.Call

class WeatherRepository(private val apiRequest: ApiRequest) {
    fun getCurrentWeather(someText : String) : Call<CurrentWeatherResponse> = apiRequest.getCurrentWeather(someText)
    fun getCurrentWeatherByCoordination(latitude : Double, longitude : Double) : Call<CurrentWeatherResponse> = apiRequest.getCurrentWeatherByCoordination(latitude, longitude)
    fun getForecasts(latitude : Double, longitude : Double) : Call<OneCallResponse> = apiRequest.getForecasts(latitude, longitude)
}
