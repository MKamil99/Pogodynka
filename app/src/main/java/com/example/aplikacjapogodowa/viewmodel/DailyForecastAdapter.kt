package com.example.aplikacjapogodowa.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aplikacjapogodowa.R
import com.example.aplikacjapogodowa.model.SpecificDayForecast
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class DailyForecastAdapter(private val specificDayForecasts : LiveData<List<SpecificDayForecast>>,
                           private val context : Context) : RecyclerView.Adapter<DailyForecastAdapter.DailyForecastHolder>() {
    inner class DailyForecastHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : DailyForecastHolder {
        return DailyForecastHolder(LayoutInflater.from(parent.context).inflate(R.layout.tile, parent, false))
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: DailyForecastHolder, position: Int) {
        // Day:
        val date = holder.itemView.findViewById<TextView>(R.id.tv_dateOrTime)
        if (position == 0) date.text = context.getString(R.string.today)
        else date.text = SimpleDateFormat("EEE").format(specificDayForecasts.value?.get(position)?.dt?.times(1000))

        // Icon:
        val icon = holder.itemView.findViewById<ImageView>(R.id.iv_weatherIcon)
        val url = "https://openweathermap.org/img/wn/${specificDayForecasts.value?.get(position)?.weather?.get(0)?.icon}@4x.png"
        Glide.with(icon).load(url).centerCrop().into(icon)

        // Minimum and maximum temperatures:
        val temperature = holder.itemView.findViewById<TextView>(R.id.tv_temperature)
        temperature.text = "Max: ${specificDayForecasts.value?.get(position)?.temp?.max?.roundToInt().toString()}°C\n" +
                "Min: ${specificDayForecasts.value?.get(position)?.temp?.min?.roundToInt().toString()}°C"
    }

    override fun getItemCount(): Int = specificDayForecasts.value?.size ?: 0
}