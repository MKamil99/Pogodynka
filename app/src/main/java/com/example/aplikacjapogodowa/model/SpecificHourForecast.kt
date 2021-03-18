package com.example.aplikacjapogodowa.model

data class SpecificHourForecast(
    val dt : Long,
    val temp : Double,
    val weather : List<Weather>
)
