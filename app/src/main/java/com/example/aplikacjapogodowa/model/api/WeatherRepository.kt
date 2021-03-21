package com.example.aplikacjapogodowa.model.api

import com.example.aplikacjapogodowa.model.responses.CurrentWeatherResponse
import com.example.aplikacjapogodowa.model.responses.OneCallResponse
import retrofit2.Call

class WeatherRepository(private val apiRequest: ApiRequest) {
    fun getCurrentWeather(someText : String) : Call<CurrentWeatherResponse> = apiRequest.getCurrentWeather(someText)
    fun getCurrentWeatherByCoordination(latitude : Double, longitude : Double) : Call<CurrentWeatherResponse> = apiRequest.getCurrentWeatherByCoordination(latitude, longitude)
    fun getForecasts(latitude : Double, longitude : Double) : Call<OneCallResponse> = apiRequest.getForecasts(latitude, longitude)
}