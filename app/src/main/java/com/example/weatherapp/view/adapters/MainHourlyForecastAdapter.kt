package com.example.weatherapp.view.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.databinding.TileBinding
import com.example.weatherapp.model.SpecificHourForecast
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

// Adapter used in displaying hourly forecast in Main Fragment:
class MainHourlyForecastAdapter : RecyclerView.Adapter<MainHourlyForecastAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = TileBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(specificHourForecasts[position])
    override fun getItemCount(): Int = specificHourForecasts.size

    inner class ViewHolder(private val binding: TileBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(item : SpecificHourForecast) {
            // Hour:
            binding.tvDateOrTime.text = SimpleDateFormat("HH:mm").format(item.dt.times(1000))

            // Icon:
            val url = "https://openweathermap.org/img/wn/${item.weather[0].icon}@4x.png"
            Glide.with(binding.root).load(url).centerCrop().into(binding.ivWeatherIcon)

            // Temperature:
            binding.tvTemperature.text = "${item.temp.roundToInt()}°C"
        }
    }

    // Stored data:
    private var specificHourForecasts = emptyList<SpecificHourForecast>()
    fun setData(newList : List<SpecificHourForecast>) {
        specificHourForecasts = newList
        notifyDataSetChanged()
    }
}
