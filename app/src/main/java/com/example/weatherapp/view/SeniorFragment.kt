package com.example.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.ConnectivityManager
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
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.SeniorScreenBinding
import com.example.weatherapp.model.responses.CurrentWeatherResponse
import com.example.weatherapp.viewmodel.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class SeniorFragment : Fragment() {
    private var _binding: SeniorScreenBinding? = null
    private val binding get() = _binding!!

    // Binding Fragment with ViewModel:
    private lateinit var weatherVM : WeatherVM
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherVM = ViewModelProvider(requireActivity()).get(WeatherVM::class.java)
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Binding with layout:
        _binding = SeniorScreenBinding.inflate(inflater, container, false)

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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect adapters with recycler views:
        binding.rvHourly.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = SeniorHourlyForecastAdapter()
        }
        binding.rvDaily.apply {
            this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = SeniorDailyForecastAdapter(requireContext())
        }

        // Top Bar Actions (changing dialog fonts based on: https://stackoverflow.com/questions/6562924/changing-font-size-into-an-alertdialog):
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
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
                    customTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    customTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    customTitle.setPadding(40, 60, 40, 20)

                    // Building the dialog:
                    val builder = MaterialAlertDialogBuilder(requireContext())
                            .setCustomTitle(customTitle)
                            .setView(customLayout)
                            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.search)) { _, _ ->
                                // Check if there is an internet connection:
                                if (!isConnectedToInternet(requireContext()))
                                {
                                    // Senior's snackbars are bigger and are displayed longer:
                                    Snackbar.make(view, resources.getString(R.string.internetNotFound), Snackbar.LENGTH_LONG).apply {
                                        val tmp = view.findViewById<TextView>(R.id.snackbar_text)
                                        tmp.textSize = 25F
                                        tmp.textAlignment = View.TEXT_ALIGNMENT_CENTER
                                    }.show()
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
                    builder.getButton(Dialog.BUTTON_NEUTRAL).textSize = 20F
                    builder.getButton(Dialog.BUTTON_NEUTRAL).setPadding(20,80,0,0)

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
                        customTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        customTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                        customTitle.setPadding(40, 60, 40, 20)

                        // Building the dialog:
                        val builder = MaterialAlertDialogBuilder(requireContext())
                                .setCustomTitle(customTitle)
                                .setMessage(resources.getString(R.string.locateDescription))
                                .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                                .setPositiveButton(resources.getString(R.string.locate)) { _, _ ->
                                    // Show info about disabled GPS (actually it checks the last location):
                                    if (weatherVM.currentLocation.value == null)
                                    {
                                        // Senior's snackbars are bigger and are displayed longer:
                                        val snack = Snackbar.make(view, resources.getString(R.string.gpsNotFound), Snackbar.LENGTH_LONG)
                                        snack.view.findViewById<TextView>(R.id.snackbar_text).textSize = 25F
                                        snack.view.findViewById<TextView>(R.id.snackbar_text).textAlignment = View.TEXT_ALIGNMENT_CENTER
                                        snack.show()
                                    }
                                    // Check internet connection:
                                    else if (!isConnectedToInternet(requireContext()))
                                    {
                                        // Senior's snackbars are bigger and are displayed longer:
                                        val snack = Snackbar.make(view, resources.getString(R.string.internetNotFound), Snackbar.LENGTH_LONG)
                                        snack.view.findViewById<TextView>(R.id.snackbar_text).textSize = 25F
                                        snack.view.findViewById<TextView>(R.id.snackbar_text).textAlignment = View.TEXT_ALIGNMENT_CENTER
                                        snack.show()
                                    }
                                    // Update weather info:
                                    else weatherVM.setCurrentWeatherByCoordination(
                                            weatherVM.currentLocation.value!!.latitude,
                                            weatherVM.currentLocation.value!!.longitude)
                                }
                                .show()

                        // Resizing message and buttons:
                        val msg = builder.findViewById<TextView>(android.R.id.message)
                        msg?.textSize = 22F
                        msg?.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        builder.getButton(Dialog.BUTTON_POSITIVE).textSize = 20F
                        builder.getButton(Dialog.BUTTON_NEUTRAL).textSize = 20F
                        builder.getButton(Dialog.BUTTON_NEUTRAL).setPadding(20,80,0,0)

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
                    customTitle.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    customTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    customTitle.setPadding(40, 60, 40, 20)

                    // Building the dialog:
                    val builder = MaterialAlertDialogBuilder(requireContext())
                            .setCustomTitle(customTitle)
                            .setMessage(resources.getString(R.string.changeDisplayDescriptionToStandard))
                            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                            .setPositiveButton(resources.getString(R.string.change)) { _, _ -> view.findNavController().navigate(R.id.action_seniorFragment_to_mainFragment) }
                            .show()

                    // Resizing message and buttons:
                    val msg = builder.findViewById<TextView>(android.R.id.message)
                    msg?.textSize = 22F
                    msg?.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    builder.getButton(Dialog.BUTTON_POSITIVE).textSize = 20F
                    builder.getButton(Dialog.BUTTON_NEUTRAL).textSize = 20F
                    builder.getButton(Dialog.BUTTON_NEUTRAL).setPadding(20,80,0,0)

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
        layoutParams.setMargins(40, 0, 40, 0)
        editText.layoutParams = layoutParams
        editText.textSize = 40F
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
