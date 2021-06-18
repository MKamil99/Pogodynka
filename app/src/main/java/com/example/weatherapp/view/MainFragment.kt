package com.example.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.MainScreenBinding
import com.example.weatherapp.model.responses.CurrentWeatherResponse
import com.example.weatherapp.viewmodel.MainDailyForecastAdapter
import com.example.weatherapp.viewmodel.MainHourlyForecastAdapter
import com.example.weatherapp.viewmodel.WeatherVM
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainFragment : Fragment() {
    private var _binding: MainScreenBinding? = null
    private val binding get() = _binding!!

    // Binding Fragment with ViewModel:
    private lateinit var weatherVM : WeatherVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherVM = ViewModelProvider(requireActivity()).get(WeatherVM::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Binding with layout:
        _binding = MainScreenBinding.inflate(inflater, container, false)

        // Hide everything by default:
        displayPlaceholder()

        // Displaying current weather:
        weatherVM.currentWeather.observe(viewLifecycleOwner, { currentWeather ->
            setMainInfo(currentWeather)
            setDetails(currentWeather)
            hidePlaceholder()
            weatherVM.setForecasts(currentWeather.coord.lat, currentWeather.coord.lon)
        })

        // Displaying hourly forecast for next 24 hours:
        weatherVM.currentHourlyForecast.observe(viewLifecycleOwner, {
            (binding.rvHourly.adapter as MainHourlyForecastAdapter).setData(it)
            binding.tvHourly.visibility = View.VISIBLE
            binding.cardHourly.visibility = View.VISIBLE
        })

        // Displaying daily forecast for next 7 weeks:
        weatherVM.currentDailyForecast.observe(viewLifecycleOwner, {
            (binding.rvDaily.adapter as MainDailyForecastAdapter).setData(it)
            binding.tvDaily.visibility = View.VISIBLE
            binding.cardDaily.visibility = View.VISIBLE
        })

        // Displaying info about wrong city:
        weatherVM.cityExists.observe(viewLifecycleOwner, {
            if (!it) {
                Snackbar.make(binding.root, resources.getString(R.string.cityNotFound), Snackbar.LENGTH_SHORT).show()
                weatherVM.cityExists.value = true
            }
        })

        // Check location:
        weatherVM.launchGPS(requireActivity(), weatherVM.currentWeather.value == null && isConnectedToInternet(requireActivity()))

        return binding.root
    }

    // Unbinding from layout:
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Displaying main info:
    @SuppressLint("SetTextI18n")
    private fun setMainInfo(currentWeather : CurrentWeatherResponse) {
        // City name, real/felt temperature and description:
        binding.tvCity.text = currentWeather.name
        binding.tvCurrentTemperature.text = "${currentWeather.main.temp.roundToInt()}°C"
        binding.tvFeelsLike.text = "${getString(R.string.feels_like)} ${currentWeather.main.feels_like.roundToInt()}°C"
        binding.tvDescription.text = currentWeather.weather[0].description.replaceFirstChar { char -> char.uppercase() }

        // Icon:
        val url = "https://openweathermap.org/img/wn/${currentWeather.weather[0].icon}@4x.png"
        Glide.with(binding.root).load(url).centerCrop().into(binding.ivCurrentWeatherIcon)
    }

    // Displaying details:
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setDetails(currentWeather: CurrentWeatherResponse) {
        binding.tvPressureValue.text = "${currentWeather.main.pressure} hPa"
        binding.tvHumidityValue.text = "${currentWeather.main.humidity}%"
        binding.tvSunriseValue.text = SimpleDateFormat("HH:mm").format(Date(currentWeather.sys.sunrise * 1000))
        binding.tvSunsetValue.text = SimpleDateFormat("HH:mm").format(Date(currentWeather.sys.sunset * 1000))
    }

    // Displaying proper view - placeholder:
    private fun displayPlaceholder() {
        binding.cardCurrent.visibility = View.GONE
        binding.tvHourly.visibility = View.GONE
        binding.cardHourly.visibility = View.GONE
        binding.tvDetails.visibility = View.GONE
        binding.cardDetails.visibility = View.GONE
        binding.tvDaily.visibility = View.GONE
        binding.cardDaily.visibility = View.GONE
        binding.tvFooter.visibility = View.GONE
        binding.placeholder.visibility = View.VISIBLE
        binding.placeholder2.visibility = View.VISIBLE
    }

    // Displaying proper view - main info:
    private fun hidePlaceholder() {
        binding.placeholder.visibility = View.GONE
        binding.placeholder2.visibility = View.GONE
        binding.cardCurrent.visibility = View.VISIBLE
        binding.tvDetails.visibility = View.VISIBLE
        binding.cardDetails.visibility = View.VISIBLE
        binding.tvFooter.visibility = View.VISIBLE
    }

    // Installing RecyclerViews and Buttons:
    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect adapters with recycler views:
        binding.rvHourly.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = MainHourlyForecastAdapter()
        }
        binding.rvDaily.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = MainDailyForecastAdapter(requireContext())
        }

        // Top Bar Actions:
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                // Find city by name and download it's weather data:
                R.id.search -> {

                    // Custom view with editText component:
                    val customLayout = makeLayout(requireContext())

                    // Building the dialog:
                    MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.searchCityTitle))
                            .setView(customLayout)
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.search)) { _, _ ->
                                // Check if there is an internet connection:
                                if (!isConnectedToInternet(requireContext()))
                                    Snackbar.make(view, resources.getString(R.string.internetNotFound), Snackbar.LENGTH_SHORT).show()
                                // Update weather
                                else {
                                    val editText = customLayout.findViewWithTag<TextInputEditText>("editTextTag")
                                    weatherVM.setCurrentWeather(editText.text.toString())
                                }
                            }
                            .show()
                    true
                }

                // Find city by current location and download it's weather data:
                R.id.findWithGPS -> {

                    // Granting permissions / finding the city:
                    weatherVM.launchGPS(requireActivity())

                    // Building the dialog:
                    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.locateTitle))
                            .setMessage(resources.getString(R.string.locateDescription))
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.locate)) { _, _ ->
                                // Show info about disabled GPS (actually it checks last location):
                                if (weatherVM.currentLocation.value == null)
                                    Snackbar.make(view, resources.getString(R.string.gpsNotFound), Snackbar.LENGTH_SHORT).show()
                                // Check internet connection:
                                else if (!isConnectedToInternet(requireContext()))
                                    Snackbar.make(view, resources.getString(R.string.internetNotFound), Snackbar.LENGTH_SHORT).show()
                                // Update weather info:
                                else weatherVM.setCurrentWeatherByCoordination(
                                        weatherVM.currentLocation.value!!.latitude,
                                        weatherVM.currentLocation.value!!.longitude)
                            }
                            .show()

                    true
                }

                // Change display mode:
                R.id.elderlyMode -> {

                    // Building the dialog:
                    MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.changeDisplayTitle))
                            .setMessage(resources.getString(R.string.changeDisplayDescriptionToSenior))
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.change)) { _, _ -> view.findNavController().navigate(R.id.action_mainFragment_to_seniorFragment) }
                            .show()

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
        layoutParams.setMargins(40, 0, 40, 0)
        editText.layoutParams = layoutParams
        editText.tag = "editTextTag"

        // Connect them and return:
        constraintLayout.addView(editText)
        return constraintLayout
    }

    // Checking connection with the internet:
    private fun isConnectedToInternet(context: Context) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null
    }
}
