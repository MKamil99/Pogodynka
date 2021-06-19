package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.MainScreenBinding
import com.example.weatherapp.model.responses.CurrentWeatherResponse
import com.example.weatherapp.view.adapters.MainDailyForecastAdapter
import com.example.weatherapp.view.adapters.MainHourlyForecastAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainFragment : AbstractFragment() {
    // Binding with layout:
    private var _binding: MainScreenBinding? = null
    private val binding get() = _binding!!
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = MainScreenBinding.inflate(inflater, container, false)

        // Hide everything by default:
        displayPlaceholder()

        // Displaying current weather:
        weatherVM.currentWeather.observe(viewLifecycleOwner, { currentWeather ->
            setMainInfo(currentWeather)
            setDetails(currentWeather)
            hidePlaceholder()
            weatherVM.setForecasts(currentWeather.coord.lat, currentWeather.coord.lon)
            binding.btnRefresh.text = SimpleDateFormat("dd.MM HH:mm").format(Date())
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
                weatherVM.setCityExists(true)
            }
        })

        // Check location:
        weatherVM.launchGPS(requireActivity())

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
        val icon = getIcon(currentWeather.weather[0].icon)
        Glide.with(binding.root).load(icon).centerCrop().into(binding.ivCurrentWeatherIcon)
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
    @SuppressLint("ResourceType", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect adapters with recycler views:
        installAdapter(binding.rvHourly, "hourly")
        installAdapter(binding.rvDaily, "daily")

        // Top Bar Actions:
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                // Find city by name and download it's weather data:
                R.id.search -> {
                    // Show the dialog:
                    val customLayout = makeEditTextLayout(requireContext(), false)
                    val editText = customLayout.findViewWithTag<TextInputEditText>("editTextTag")
                    MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.searchCityTitle))
                            .setView(customLayout)
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.search)) { _, _ -> searchAction(view, editText, false) }
                            .show()
                    true
                }

                // Find city by current location and download it's weather data:
                R.id.findWithGPS -> {
                    // Granting permissions / finding the city:
                    weatherVM.launchGPS(requireActivity())

                    // If there are permissions...
                    if (checkPermissions())
                    // ... show the dialog:
                            MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.locateTitle))
                            .setMessage(resources.getString(R.string.locateDescription))
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.locate)) { _, _ -> locateAction(view, false) }
                            .show()

                    true
                }

                // Change display mode:
                R.id.elderlyMode -> {
                    // Show the dialog:
                    MaterialAlertDialogBuilder(requireContext())
                            .setTitle(resources.getString(R.string.changeDisplayTitle))
                            .setMessage(resources.getString(R.string.changeDisplayDescriptionToSenior))
                            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.change)) { _, _ -> changeDisplayAction(view, false) }
                            .show()
                    true
                }

                else -> false
            }
        }

        // Refresh Button:
        binding.btnRefresh.setOnClickListener {
            weatherVM.setForecasts(weatherVM.currentWeather.value?.coord?.lat, weatherVM.currentWeather.value?.coord?.lon)
            binding.btnRefresh.text = SimpleDateFormat("dd.MM HH:mm").format(Date())
        }
    }

    // Adding Linear Layout and Adapter to Recycler View:
    private fun installAdapter(recyclerView: RecyclerView, rvType: String) {
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter =
                if (rvType == "hourly") MainHourlyForecastAdapter(::getIcon)
                else MainDailyForecastAdapter(requireContext(), ::getIcon)
        }
    }
}
