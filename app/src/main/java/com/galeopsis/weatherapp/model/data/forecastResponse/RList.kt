package com.galeopsis.weatherapp.model.data.forecastResponse

import com.google.gson.annotations.SerializedName

data class RList(
    @SerializedName("clouds")
    val clouds: RClouds?,
    val dt: Int,
    @SerializedName("dt_txt")
    val dtTxt: String,
    val main: RMain,
    val pop: Double?,
    @SerializedName("rain")
    val rain: RRain?,
    @SerializedName("sys")
    val sys: RSys?,
    val visibility: Int?,
    val wind: RWind,
    val weather: List<RWeather?>
)
