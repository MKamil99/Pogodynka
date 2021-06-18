package com.example.weatherapp.model.responses

import com.example.weatherapp.model.Coordination
import com.example.weatherapp.model.Details
import com.example.weatherapp.model.SunTimes
import com.example.weatherapp.model.Weather

data class CurrentWeatherResponse(
    val cod : String,
    val coord : Coordination,
    val weather : List<Weather>,
    val main : Details,
    val dt : Long,
    val sys : SunTimes,
    val name : String
)
