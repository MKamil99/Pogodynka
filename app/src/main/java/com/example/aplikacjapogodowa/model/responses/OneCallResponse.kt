package com.example.aplikacjapogodowa.model.responses

import com.example.aplikacjapogodowa.model.SpecificDayForecast
import com.example.aplikacjapogodowa.model.SpecificHourForecast

data class OneCallResponse(
    val lat : Double,
    val lon : Double,
    val timezone_offset : Int,
    val hourly : List<SpecificHourForecast>,
    val daily : List<SpecificDayForecast>
)