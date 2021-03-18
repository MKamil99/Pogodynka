package com.example.aplikacjapogodowa.model.api

import com.example.aplikacjapogodowa.model.responses.CurrentWeatherResponse
import com.example.aplikacjapogodowa.model.responses.OneCallResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequest {
    @GET("weather?lang=$lang&units=$units&appid=$apiKey")
    fun getCurrentWeather(@Query("q") someText : String) : Call<CurrentWeatherResponse>

    @GET("onecall?lang=$lang&units=$units&exclude=current,minutely,alerts&appid=$apiKey")
    fun getForecasts(@Query("lat") lat : Double, @Query("lon") lon : Double) : Call<OneCallResponse>

    //                  INSTANCE WHICH PROVIDES COMMUNICATION WITH API
    companion object {
        private const val WEBSITE = "https://api.openweathermap.org/data/2.5/"
        private const val apiKey = "YOUR_API_KEY"     // place your apiKey here
        private const val units = "metric"
        private const val lang = "pl"
        private var INSTANCE : ApiRequest? = null

        fun getAPI() : ApiRequest {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance
            else {
                val comm = Retrofit.Builder()
                    .baseUrl(WEBSITE).addConverterFactory(GsonConverterFactory.create())
                    .build().create(ApiRequest::class.java)
                INSTANCE = comm
                return comm
            }
        }
    }
}