package com.example.aplikacjapogodowa.viewmodel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.aplikacjapogodowa.model.SpecificDayForecast
import com.example.aplikacjapogodowa.model.SpecificHourForecast
import com.example.aplikacjapogodowa.model.api.ApiRequest
import com.example.aplikacjapogodowa.model.api.WeatherRepository
import com.example.aplikacjapogodowa.model.responses.CurrentWeatherResponse
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

// ViewModel used in this application for connecting views with models:
class WeatherVM(application: Application) : AndroidViewModel(application) {
    private val repository : WeatherRepository = WeatherRepository(ApiRequest.getAPI())

    // Current weather:
    var currentWeather = MutableLiveData<CurrentWeatherResponse>()
    var cityExists = MutableLiveData<Boolean>()
    fun setCurrentWeather(cityName : String)
    {
        viewModelScope.launch {
            val response = repository.getCurrentWeather(cityName).awaitResponse()
            cityExists.value = response.isSuccessful
            if (response.isSuccessful)
            {
                val data = response.body()!!
                currentWeather.value = data
            }
        }
    }
    fun setCurrentWeatherByCoordination(latitude : Double, longitude : Double)
    {
        viewModelScope.launch {
            val response = repository.getCurrentWeatherByCoordination(latitude, longitude).awaitResponse()
            cityExists.value = response.isSuccessful
            if (response.isSuccessful)
            {
                val data = response.body()!!
                currentWeather.value = data
            }
        }
    }


    // Forecasts (hourly for 24 hours and daily for 7 days):
    var currentHourlyForecast = MutableLiveData<List<SpecificHourForecast>>()
    var currentDailyForecast = MutableLiveData<List<SpecificDayForecast>>()
    fun setForecasts(latitude : Double, longitude : Double)
    {
        viewModelScope.launch {
            val response = repository.getForecasts(latitude, longitude).awaitResponse()
            if (response.isSuccessful)
            {
                val data = response.body()!!
                currentHourlyForecast.value = data.hourly.subList(1, 25)
                currentDailyForecast.value = data.daily.subList(0, 7)
            }
        }
    }


    // Current location:
    var currentLocation = MutableLiveData<Location>()
    fun launchGPS(activity: Activity, searchNow : Boolean = false) {

        // Request permissions to use GPS: https://www.tutorialspoint.com/how-to-get-the-current-gps-location-programmatically-on-android-using-kotlin
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2) }
            return
        }

        // Update location:
        LocationServices.getFusedLocationProviderClient(activity).lastLocation.addOnSuccessListener {
            location: Location? ->
            if (location != null) {
                currentLocation.value = location
                // Get current location's current weather if app has just been launched:
                if (searchNow && currentWeather.value == null)
                    setCurrentWeatherByCoordination(currentLocation.value!!.latitude, currentLocation.value!!.longitude)
            }
        }
    }
}