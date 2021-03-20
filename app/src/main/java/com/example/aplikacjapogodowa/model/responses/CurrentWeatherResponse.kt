package com.example.aplikacjapogodowa.model.responses

import com.example.aplikacjapogodowa.model.*

data class CurrentWeatherResponse(
    val cod : String,
    val coord : Coordination,
    val weather : List<Weather>,
    val main : Details,
    val dt : Long,
    val sys : SunTimes,
    val name : String
)
