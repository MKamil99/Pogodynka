package com.example.weatherapp.model.api

import com.example.weatherapp.model.responses.CurrentWeatherResponse
import com.example.weatherapp.model.responses.OneCallResponse
import retrofit2.Call

class WeatherRepository(private val apiRequest: ApiRequest) {
    fun getCurrentWeather(someText : String, lang : String) : Call<CurrentWeatherResponse> {
        return apiRequest.getCurrentWeather(someText, lang)
    }
    fun getCurrentWeatherByCoordination(latitude : Double, longitude : Double, lang : String) : Call<CurrentWeatherResponse> {
        return apiRequest.getCurrentWeatherByCoordination(latitude, longitude, lang)
    }
    fun getForecasts(latitude : Double, longitude : Double, lang : String) : Call<OneCallResponse> {
      return apiRequest.getForecasts(latitude, longitude, lang)
    }
}
