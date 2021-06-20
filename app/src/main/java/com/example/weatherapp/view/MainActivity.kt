package com.example.weatherapp.view

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.viewmodel.LocationVM
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
    }

    // Reaction for granting GPS permissions:
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ViewModelProvider(this).get(LocationVM::class.java).launchGPS(this)
        }
    }
}
