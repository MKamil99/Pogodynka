package com.example.weatherapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.viewmodel.WeatherVM

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherVM: WeatherVM
    override fun onCreate(savedInstanceState: Bundle?) {
        // Binding with layout:
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gathering data from Room:
        weatherVM = ViewModelProvider(this).get(WeatherVM::class.java)
        weatherVM.weatherInfo.observe(this, {
            if (it != null && weatherVM.currentWeather.value == null)
                weatherVM.setCurrentWeatherByRoomData(it)
        })
        weatherVM.hourForecast.observe(this, {
            if (it != null && weatherVM.currentHourlyForecast.value == null)
                weatherVM.setHourlyForecastByRoomData(it)
        })
        weatherVM.dayForecast.observe(this, {
            if (it != null && weatherVM.currentDailyForecast.value == null)
                weatherVM.setDailyForecastByRoomData(it)
        })
    }
}
