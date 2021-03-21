package com.example.aplikacjapogodowa.view

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.aplikacjapogodowa.R
import com.example.aplikacjapogodowa.viewmodel.WeatherVM

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // Reaction for granting GPS permissions:
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 2 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val weatherVM = ViewModelProvider(this).get(WeatherVM::class.java)
            weatherVM.launchGPS(this)
        }
    }
}