package com.example.pogodynka.model

data class SpecificHourForecast(
    val dt : Long,
    val temp : Double,
    val weather : List<Weather>
)
