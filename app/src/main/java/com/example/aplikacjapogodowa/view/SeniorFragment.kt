package com.example.aplikacjapogodowa.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplikacjapogodowa.R
import com.example.aplikacjapogodowa.viewmodel.DailyForecastAdapter
import com.example.aplikacjapogodowa.viewmodel.HourlyForecastAdapter
import com.example.aplikacjapogodowa.viewmodel.WeatherVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.main_screen.*
import kotlinx.android.synthetic.main.main_screen.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class SeniorFragment : Fragment() {
    // ViewModel:
    private lateinit var weatherVM : WeatherVM

    // Hourly Forecast:
    private lateinit var hourlyForecastAdapter : HourlyForecastAdapter
    private lateinit var hourlyForecastLayoutManager : LinearLayoutManager
    private lateinit var hourlyForecastRecyclerView : RecyclerView

    // Daily Forecast:
    private lateinit var dailyForecastAdapter : DailyForecastAdapter
    private lateinit var dailyForecastLayoutManager : LinearLayoutManager
    private lateinit var dailyForecastRecyclerView : RecyclerView


    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        weatherVM = ViewModelProvider(requireActivity()).get(WeatherVM::class.java)
        hourlyForecastLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        hourlyForecastAdapter = HourlyForecastAdapter(weatherVM.currentHourlyForecast, "Senior")
        dailyForecastLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        dailyForecastAdapter = DailyForecastAdapter(weatherVM.currentDailyForecast, "Senior", requireContext())
        val view =  inflater.inflate(R.layout.senior_screen, container, false)

        // Hide everything by default:
        view.card_current.visibility = View.GONE
        view.tv_hourly.visibility = View.GONE
        view.card_hourly.visibility = View.GONE
        view.tv_details.visibility = View.GONE
        view.card_details.visibility = View.GONE
        view.tv_daily.visibility = View.GONE
        view.card_daily.visibility = View.GONE
        view.placeholder.visibility = View.VISIBLE
        view.placeholder2.visibility = View.VISIBLE

        // Displaying current weather:
        weatherVM.currentWeather.observe(viewLifecycleOwner, {
            // Main:
            view.tv_city.text = it.name
            view.tv_currentTemperature.text = "${it.main.temp.roundToInt()}°C"
            view.tv_feelsLike.text = "Odczuwalne: ${it.main.feels_like.roundToInt()}°C"
            view.tv_description.text = it.weather[0].description.capitalize(Locale.ROOT)

            // Icon:
            val url = "https://openweathermap.org/img/wn/${it.weather[0].icon}@4x.png"
            Glide.with(view.iv_currentWeatherIcon).load(url).centerCrop().into(view.iv_currentWeatherIcon)

            // Details:
            view.tv_pressureValue.text = "${it.main.pressure} hPa"
            view.tv_sunriseValue.text = SimpleDateFormat("HH:mm").format(Date(it.sys.sunrise * 1000))
            view.tv_sunsetValue.text = SimpleDateFormat("HH:mm").format(Date(it.sys.sunset * 1000))

            // Update forecasts:
            weatherVM.setForecasts(it.coord.lat, it.coord.lon)

            // Display it:
            view.placeholder.visibility = View.GONE
            view.placeholder2.visibility = View.GONE
            view.card_current.visibility = View.VISIBLE
            view.tv_details.visibility = View.VISIBLE
            view.card_details.visibility = View.VISIBLE
        })

        // Displaying hourly forecast for next 24 hours:
        weatherVM.currentHourlyForecast.observe(viewLifecycleOwner, {
            hourlyForecastAdapter.notifyDataSetChanged()
            view.tv_hourly.visibility = View.VISIBLE
            view.card_hourly.visibility = View.VISIBLE
        })

        // Displaying daily forecast for next 7 weeks:
        weatherVM.currentDailyForecast.observe(viewLifecycleOwner, {
            dailyForecastAdapter.notifyDataSetChanged()
            view.tv_daily.visibility = View.VISIBLE
            view.card_daily.visibility = View.VISIBLE
        })

        // Displaying info about wrong city:
        weatherVM.cityExists.observe(viewLifecycleOwner, {
            if (!it) Snackbar.make(view, resources.getString(R.string.cityNotFound), Snackbar.LENGTH_LONG).show()
        })

        // Check location:
        weatherVM.launchGPS(requireActivity(), weatherVM.currentWeather.value == null && isConnectedToInternet(requireActivity()))

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect adapters with recycler views:
        hourlyForecastRecyclerView = rv_hourly.apply {
            this.layoutManager = hourlyForecastLayoutManager
            this.adapter = hourlyForecastAdapter
        }
        dailyForecastRecyclerView = rv_daily.apply {
            this.layoutManager = dailyForecastLayoutManager
            this.adapter = dailyForecastAdapter
        }

        // Top Bar Actions (changing dialog fonts based on: https://stackoverflow.com/questions/6562924/changing-font-size-into-an-alertdialog):
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                // Find city by name and download it's weather data:
                R.id.search -> {

                    // Custom view with editText component:
                    val customLayout = makeLayout(requireContext())

                    // Custom title with bigger font for seniors (based on:
                    // https://stackoverflow.com/questions/28643277/dialog-box-title-text-size-in-android):
                    val customTitle = TextView(requireContext())
                    customTitle.text = resources.getString(R.string.searchCityTitle)
                    customTitle.textSize = 22F
                    customTitle.typeface = Typeface.DEFAULT_BOLD
                    customTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    customTitle.setPadding(60, 60, 60, 20);

                    // Building the dialog:
                    val builder = MaterialAlertDialogBuilder(requireContext())
                            .setCustomTitle(customTitle)
                            .setView(customLayout)
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.search)) { _, _ ->
                                // Check if there is an internet connection:
                                if (!isConnectedToInternet(requireContext()))
                                {
                                    // Senior's snackbars are bigger and are displayed longer:
                                    val snack = Snackbar.make(view, resources.getString(R.string.internetNotFound), Snackbar.LENGTH_LONG)
                                    snack.view.findViewById<TextView>(R.id.snackbar_text).textSize = 20F
                                    snack.show()

                                }
                                // Update weather
                                else
                                {
                                    val editText = customLayout.findViewWithTag<TextInputEditText>("editTextTag")
                                    weatherVM.setCurrentWeather(editText.text.toString())
                                }
                            }
                            .show()

                    // Resizing buttons:
                    builder.getButton(Dialog.BUTTON_POSITIVE).textSize = 20F
                    builder.getButton(Dialog.BUTTON_NEGATIVE).textSize = 20F
                    builder.getButton(Dialog.BUTTON_NEGATIVE).setPadding(0,80,0,0)

                    true
                }

                // Find city by current location and download it's weather data:
                R.id.findWithGPS -> {

                    // Try to grant permissions and find city:
                    weatherVM.launchGPS(requireActivity())

                    // Show dialog:
                    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        // Custom title with bigger font for seniors:
                        val customTitle = TextView(requireContext())
                        customTitle.text = resources.getString(R.string.locateTitle)
                        customTitle.textSize = 22F
                        customTitle.typeface = Typeface.DEFAULT_BOLD
                        customTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        customTitle.setPadding(60, 60, 60, 20);

                        // Building the dialog:
                        val builder = MaterialAlertDialogBuilder(requireContext())
                                .setCustomTitle(customTitle)
                                .setMessage(resources.getString(R.string.locateDescription))
                                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                                .setPositiveButton(resources.getString(R.string.locate)) { _, _ ->
                                    // Show info about disabled GPS (actually it checks the last location):
                                    if (weatherVM.currentLocation.value == null)
                                    {
                                        // Senior's snackbars are bigger and are displayed longer:
                                        val snack = Snackbar.make(view, resources.getString(R.string.gpsNotFound), Snackbar.LENGTH_LONG)
                                        snack.view.findViewById<TextView>(R.id.snackbar_text).textSize = 20F
                                        snack.show()
                                    }
                                    // Check internet connection:
                                    else if (!isConnectedToInternet(requireContext()))
                                    {
                                        // Senior's snackbars are bigger and are displayed longer:
                                        val snack = Snackbar.make(view, resources.getString(R.string.internetNotFound), Snackbar.LENGTH_LONG)
                                        snack.view.findViewById<TextView>(R.id.snackbar_text).textSize = 20F
                                        snack.show()
                                    }
                                    // Update weather info:
                                    else weatherVM.setCurrentWeatherByCoordination(
                                            weatherVM.currentLocation.value!!.latitude,
                                            weatherVM.currentLocation.value!!.longitude)
                                }
                                .show()

                        // Resizing message and buttons:
                        builder.findViewById<TextView>(android.R.id.message)?.textSize = 22F
                        builder.getButton(Dialog.BUTTON_POSITIVE).textSize = 20F
                        builder.getButton(Dialog.BUTTON_NEGATIVE).textSize = 20F
                        builder.getButton(Dialog.BUTTON_NEGATIVE).setPadding(0,80,0,0)

                        // Changing message color to black (based on:
                        // https://stackoverflow.com/questions/31590714/getcolorint-id-deprecated-on-android-6-0-marshmallow-api-23):
                        builder.findViewById<TextView>(android.R.id.message)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    }

                    true
                }

                // Change display mode:
                R.id.elderlyMode -> {

                    // Custom title with bigger font for seniors:
                    val customTitle = TextView(requireContext())
                    customTitle.text = resources.getString(R.string.changeDisplayTitle)
                    customTitle.textSize = 22F
                    customTitle.typeface = Typeface.DEFAULT_BOLD
                    customTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    customTitle.setPadding(60, 60, 60, 20);

                    // Building the dialog:
                    val builder = MaterialAlertDialogBuilder(requireContext())
                            .setCustomTitle(customTitle)
                            .setMessage(resources.getString(R.string.changeDisplayDescriptionToStandard))
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.change)) { _, _ -> view.findNavController().navigate(R.id.action_seniorFragment_to_mainFragment) }
                            .show()

                    // Resizing message and buttons:
                    builder.findViewById<TextView>(android.R.id.message)?.textSize = 22F
                    builder.getButton(Dialog.BUTTON_POSITIVE).textSize = 20F
                    builder.getButton(Dialog.BUTTON_NEGATIVE).textSize = 20F
                    builder.getButton(Dialog.BUTTON_NEGATIVE).setPadding(0,80,0,0)

                    // Changing message color to black:
                    builder.findViewById<TextView>(android.R.id.message)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                    true
                }

                else -> false
            }
        }
    }



    // Function responsible for adding input field in "searching city" dialog
    // (based on: https://android--code.blogspot.com/2020/03/android-kotlin-alertdialog-edittext.html):
    private fun makeLayout(context: Context) : ConstraintLayout {
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
        layoutParams.setMargins(50, 0, 50, 0)
        editText.layoutParams = layoutParams
        editText.textSize = 40F
        editText.tag = "editTextTag"

        // Connect them and return:
        constraintLayout.addView(editText)
        return constraintLayout
    }


    // Checking connection with the internet (based on: https://developer.android.com/training/monitoring-device-state/connectivity-status-type):
    private fun isConnectedToInternet(context: Context) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}