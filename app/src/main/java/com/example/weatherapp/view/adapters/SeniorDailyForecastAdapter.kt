package com.example.weatherapp.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.TileBigBinding
import com.example.weatherapp.model.SpecificDayForecast
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

// Adapter used in displaying daily forecast in Senior Fragment:
class SeniorDailyForecastAdapter(private val context : Context) : RecyclerView.Adapter<SeniorDailyForecastAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val view = TileBigBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(specificDayForecasts[position], position)
    override fun getItemCount(): Int = specificDayForecasts.size

    inner class ViewHolder(private val binding: TileBigBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(item : SpecificDayForecast, position: Int) {
            // Date:
            binding.tvDateOrTime.text =
                if (position == 0) context.getString(R.string.today)
                else SimpleDateFormat("EEEE").format(item.dt.times(1000))

            // Icon:
            val url = "https://openweathermap.org/img/wn/${item.weather[0].icon}@4x.png"
            Glide.with(binding.root).load(url).centerCrop().into(binding.ivWeatherIcon)

            // Minimum and maximum temperatures:
            binding.tvTemperature.text = "Max: ${item.temp.max.roundToInt()}°C\n Min: ${item.temp.min.roundToInt()}°C"
        }
    }

    // Stored data:
    private var specificDayForecasts = emptyList<SpecificDayForecast>()
    fun setData(newList : List<SpecificDayForecast>) {
        specificDayForecasts = newList
        notifyDataSetChanged()
    }
}
