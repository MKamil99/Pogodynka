package com.example.weatherapp.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.viewmodel.LocationWithWeatherVM
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

// Fragment for methods that are the same in both Main Fragment and Senior Fragment (without binding):
abstract class AbstractFragment : Fragment() {
    // Binding Fragment with ViewModel:
    protected lateinit var weatherVM : LocationWithWeatherVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherVM = ViewModelProvider(requireActivity()).get(LocationWithWeatherVM::class.java)
    }


    //                                    CREATING VIEWS

    // Function responsible for adding input field in "searching city" dialog
    // (based on: https://android--code.blogspot.com/2020/03/android-kotlin-alertdialog-edittext.html):
    protected fun makeEditTextLayout(context: Context, isSeniorMode: Boolean) : ConstraintLayout {
        // Prepare height-width parameters for layout and input:
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        // Prepare layout (editTextInput's parent):
        val constraintLayout = ConstraintLayout(context)
        constraintLayout.layoutParams = layoutParams

        // Prepare editTextInput:
        val editText = TextInputEditText(context)
        layoutParams.setMargins(40, 0, 40, 0)
        editText.layoutParams = layoutParams
        if (isSeniorMode) editText.textSize = 40F
        editText.tag = "editTextTag"

        // Connect them and return:
        constraintLayout.addView(editText)
        return constraintLayout
    }

    // Displaying snackbar with appropriate message and design
    // (senior's snackbars are bigger and are displayed longer):
    private fun makeSnackbar(view : View, message : String, isSeniorMode : Boolean) {
        Snackbar.make(view, message, if (isSeniorMode) Snackbar.LENGTH_LONG else Snackbar.LENGTH_SHORT).apply {
            if (isSeniorMode) {
                val tmp = view.findViewById<TextView>(R.id.snackbar_text)
                tmp.textSize = 25F
                tmp.textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
        }.show()
    }


    //                                   APP BAR ACTIONS

    // Result of clicking Search in Search Dialog:
    protected fun searchAction(view : View, editText: TextInputEditText, isSeniorMode: Boolean) {
        // Check if there is an internet connection:
        if (!isConnectedToInternet(requireContext()))
            makeSnackbar(view, resources.getString(R.string.internetNotFound), isSeniorMode)
        // Update weather:
        else weatherVM.setCurrentWeather(editText.text.toString())
    }

    // Result of clicking Locate in Localization Dialog:
    protected fun locateAction(view : View, isSeniorMode: Boolean) {
        // Show info about disabled GPS (actually it checks the last location):
        if (weatherVM.currentLocation.value == null)
            makeSnackbar(view, resources.getString(R.string.gpsNotFound), isSeniorMode)
        // Check internet connection:
        else if (!isConnectedToInternet(requireContext()))
            makeSnackbar(view, resources.getString(R.string.internetNotFound), isSeniorMode)
        // Update weather info:
        else weatherVM.setCurrentWeatherByCoordination(
            weatherVM.currentLocation.value!!.latitude,
            weatherVM.currentLocation.value!!.longitude)
    }

    // Result of clicking Change in Changing Display Dialog:
    protected fun changeDisplayAction(view: View, isSeniorMode: Boolean) {
        if (isSeniorMode) view.findNavController().navigate(R.id.action_seniorFragment_to_mainFragment)
        else view.findNavController().navigate(R.id.action_mainFragment_to_seniorFragment)
    }


    //                                     CONDITIONS

    // Checking connection with the internet:
    protected fun isConnectedToInternet(context: Context) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null
    }

    // Checking permissions to use localization:
    protected fun checkPermissions() : Boolean {
        return ActivityCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}