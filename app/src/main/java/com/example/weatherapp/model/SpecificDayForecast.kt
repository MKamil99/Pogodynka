package com.example.weatherapp.model

data class SpecificDayForecast(
    val dt : Long,
    val temp : Temperatures,
    val weather : List<Weather>
)
