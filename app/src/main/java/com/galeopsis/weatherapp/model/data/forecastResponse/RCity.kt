package com.galeopsis.weatherapp.model.data.forecastResponse

import com.google.gson.annotations.SerializedName

data class RCity(
    @SerializedName("coord")
    val coord: RCoord?,
    val country: String?,
    val id: Int?,
    val name: String?,
    val population: Int?,
    val sunrise: Int?,
    val sunset: Int?,
    val timezone: Int?
)
