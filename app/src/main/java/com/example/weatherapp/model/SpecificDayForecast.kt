package com.example.weatherapp.model

data class SpecificDayForecast(
    val dt : Long,
    val sunrise : Long,
    val sunset : Long,
    val temp : Temperatures,
    val weather : List<Weather>
)
