package com.example.pogodynka.model.responses

import com.example.pogodynka.model.SpecificDayForecast
import com.example.pogodynka.model.SpecificHourForecast

data class OneCallResponse(
    val lat : Double,
    val lon : Double,
    val hourly : List<SpecificHourForecast>,
    val daily : List<SpecificDayForecast>
)