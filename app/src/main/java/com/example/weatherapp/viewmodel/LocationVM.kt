package com.example.weatherapp.viewmodel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices

// View Model which adds location features to all classes that inherits from it:
abstract class LocationVM(application: Application) : AndroidViewModel(application) {
    // Current location:
    private val mutCurrentLocation = MutableLiveData<Location>()
    val currentLocation : LiveData<Location> get() = mutCurrentLocation
    fun launchGPS(activity: Activity, searchNow : Boolean = false) {
        // Request permissions to use GPS (based on: https://www.tutorialspoint.com/how-to-get-the-current-gps-location-programmatically-on-android-using-kotlin):
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if ((ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 2)
            }
            return
        }

        // Update location:
        LocationServices.getFusedLocationProviderClient(activity).lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) mutCurrentLocation.value = location
        }
    }
}
