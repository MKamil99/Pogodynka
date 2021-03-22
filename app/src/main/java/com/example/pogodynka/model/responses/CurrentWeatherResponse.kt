package com.example.pogodynka.model.responses

import com.example.pogodynka.model.*

data class CurrentWeatherResponse(
    val cod : String,
    val coord : Coordination,
    val weather : List<Weather>,
    val main : Details,
    val dt : Long,
    val sys : SunTimes,
    val name : String
)
