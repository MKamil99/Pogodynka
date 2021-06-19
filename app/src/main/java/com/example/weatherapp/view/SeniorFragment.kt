package com.example.weatherapp.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.SeniorScreenBinding
import com.example.weatherapp.model.responses.CurrentWeatherResponse
import com.example.weatherapp.view.adapters.SeniorDailyForecastAdapter
import com.example.weatherapp.view.adapters.SeniorHourlyForecastAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class SeniorFragment : AbstractFragment() {
    // Binding with layout:
    private var _binding: SeniorScreenBinding? = null
    private val binding get() = _binding!!
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SeniorScreenBinding.inflate(inflater, container, false)

        // Hide everything by default:
        displayPlaceholder()

        // Displaying current weather:
        weatherVM.currentWeather.observe(viewLifecycleOwner, { currentWeather ->
            setMainInfo(currentWeather)
            setDetails(currentWeather)
            hidePlaceholder()
            weatherVM.setForecasts(currentWeather.coord.lat, currentWeather.coord.lon)
            binding.btnRefresh.text = "${getString(R.string.refreshed)} ${SimpleDateFormat("dd.MM HH:mm").format(Date())}"
        })

        // Displaying hourly forecast for next 24 hours:
        weatherVM.currentHourlyForecast.observe(viewLifecycleOwner, {
            (binding.rvHourly.adapter as SeniorHourlyForecastAdapter).setData(it)
            binding.tvHourly.visibility = View.VISIBLE
            binding.cardHourly.visibility = View.VISIBLE
        })

        // Displaying daily forecast for next 7 weeks:
        weatherVM.currentDailyForecast.observe(viewLifecycleOwner, {
            (binding.rvDaily.adapter as SeniorDailyForecastAdapter).setData(it)
            binding.tvDaily.visibility = View.VISIBLE
            binding.cardDaily.visibility = View.VISIBLE
        })

        // Displaying info about wrong city:
        weatherVM.cityExists.observe(viewLifecycleOwner, {
            if (!it) {
                Snackbar.make(binding.root, resources.getString(R.string.cityNotFound), Snackbar.LENGTH_LONG).apply {
                    val tmp = view.findViewById<TextView>(R.id.snackbar_text)
                    tmp.textSize = 25F
                    tmp.textAlignment = View.TEXT_ALIGNMENT_CENTER
                }.show()
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
    }

    // Installing RecyclerViews and Buttons:
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect adapters with recycler views:
        installAdapter(binding.rvHourly, "hourly")
        installAdapter(binding.rvDaily, "daily")

        // Top Bar Actions (changing dialog fonts based on: https://stackoverflow.com/questions/6562924/changing-font-size-into-an-alertdialog):
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {

                // Find city by name and download it's weather data:
                R.id.search -> {
                    // Show the dialog:
                    val customLayout = makeEditTextLayout(requireContext(), true)
                    val editText = customLayout.findViewWithTag<TextInputEditText>("editTextTag")
                    MaterialAlertDialogBuilder(requireContext())
                        .setCustomTitle(makeCustomTitle(resources.getString(R.string.searchCityTitle)))
                        .setView(customLayout)
                        .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(resources.getString(R.string.search)) { _, _ -> searchAction(view, editText, true) }
                        .show()
                        .apply { resizeDialog(this) }
                    true
                }

                // Find city by current location and download it's weather data:
                R.id.findWithGPS -> {
                    // Try to grant permissions and find city:
                    weatherVM.launchGPS(requireActivity())

                    // If there are permissions...
                    if (checkPermissions()) {
                        // ... show the dialog:
                        MaterialAlertDialogBuilder(requireContext())
                            .setCustomTitle(makeCustomTitle(resources.getString(R.string.locateTitle)))
                            .setMessage(resources.getString(R.string.locateDescription))
                            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.locate)) { _, _ -> locateAction(view, true) }
                            .show()
                            .apply { resizeDialog(this) }
                    }
                    true
                }

                // Change display mode:
                R.id.elderlyMode -> {
                    // Show the dialog:
                    MaterialAlertDialogBuilder(requireContext())
                        .setCustomTitle(makeCustomTitle(resources.getString(R.string.changeDisplayTitle)))
                        .setMessage(resources.getString(R.string.changeDisplayDescriptionToStandard))
                        .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton(resources.getString(R.string.change)) { _, _ -> changeDisplayAction(view, true) }
                        .show()
                        .apply { resizeDialog(this) }
                    true
                }

                else -> false
            }
        }

        // Refresh Button:
        binding.btnRefresh.setOnClickListener {
            weatherVM.setForecasts(weatherVM.currentWeather.value?.coord?.lat, weatherVM.currentWeather.value?.coord?.lon)
            binding.btnRefresh.text = "${getString(R.string.refreshed)} ${SimpleDateFormat("dd.MM HH:mm").format(Date())}"
        }
    }

    // Adding Linear Layout and Adapter to Recycler View:
    private fun installAdapter(recyclerView: RecyclerView, rvType: String) {
        recyclerView.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter =
                if (rvType == "hourly") SeniorHourlyForecastAdapter(::getIcon)
                else SeniorDailyForecastAdapter(requireContext(), ::getIcon)
        }
    }

    // Adding custom title with bigger font (based on:
    // https://stackoverflow.com/questions/28643277/dialog-box-title-text-size-in-android):
    private fun makeCustomTitle(title: String): TextView {
        return TextView(requireContext()).apply {
            text = title
            textSize = 22F
            typeface = Typeface.DEFAULT_BOLD
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setPadding(40, 60, 40, 20)
        }
    }

    // Resizing dialog elements (for seniors):
    private fun resizeDialog(dialog: androidx.appcompat.app.AlertDialog) {
        // Resizing message and changing its color to black:
        dialog.findViewById<TextView>(android.R.id.message).apply {
            this?.textSize = 22F
            this?.textAlignment = View.TEXT_ALIGNMENT_CENTER
            this?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }

        // Resizing buttons of the dialog:
        dialog.getButton(Dialog.BUTTON_POSITIVE).textSize = 20F
        dialog.getButton(Dialog.BUTTON_NEUTRAL).textSize = 20F
        dialog.getButton(Dialog.BUTTON_NEUTRAL).setPadding(20,80,0,0)
    }
}
