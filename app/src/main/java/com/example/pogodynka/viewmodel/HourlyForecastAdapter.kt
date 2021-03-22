package com.example.pogodynka.viewmodel

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pogodynka.R
import com.example.pogodynka.model.SpecificHourForecast
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

// Adapter used in displaying hourly forecast:
class HourlyForecastAdapter(private val specificHourForecasts : LiveData<List<SpecificHourForecast>>,
                            private val inFragment : String) : RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastHolder>() {
    inner class HourlyForecastHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : HourlyForecastHolder {
        return if (inFragment == "Main")
            HourlyForecastHolder(LayoutInflater.from(parent.context).inflate(R.layout.tile, parent, false))
        else    // inFragment == "Senior"
            HourlyForecastHolder(LayoutInflater.from(parent.context).inflate(R.layout.tile_big, parent, false))
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: HourlyForecastHolder, position: Int) {
        // Hour:
        val date = holder.itemView.findViewById<TextView>(R.id.tv_dateOrTime)
        date.text = SimpleDateFormat("HH:mm").format(specificHourForecasts.value?.get(position)?.dt?.times(1000))

        // Icon:
        val icon = holder.itemView.findViewById<ImageView>(R.id.iv_weatherIcon)
        val url = "https://openweathermap.org/img/wn/${specificHourForecasts.value?.get(position)?.weather?.get(0)?.icon}@4x.png"
        Glide.with(icon).load(url).centerCrop().into(icon)

        // Temperature:
        val temperature = holder.itemView.findViewById<TextView>(R.id.tv_temperature)
        temperature.text = "${specificHourForecasts.value?.get(position)?.temp?.roundToInt().toString()}°C"
    }

    override fun getItemCount(): Int = specificHourForecasts.value?.size ?: 0
}