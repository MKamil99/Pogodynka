package com.example.weatherapp.model.responses

import com.example.weatherapp.model.SpecificDayForecast
import com.example.weatherapp.model.SpecificHourForecast

data class OneCallResponse(
    val lat : Double,
    val lon : Double,
    val hourly : List<SpecificHourForecast>,
    val daily : List<SpecificDayForecast>
)
