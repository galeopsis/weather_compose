package com.galeopsis.weatherapp.utils

import androidx.room.TypeConverter
import com.galeopsis.weatherapp.model.data.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {

    private val gson = Gson()
    private val weatherListType = object : TypeToken<List<Weather?>>() {}.type

    @TypeConverter
    fun listToJson(weather: List<Weather?>?): String? {
        return weather?.takeIf { it.isNotEmpty() }?.let { gson.toJson(it, weatherListType) }
    }

    @TypeConverter
    fun jsonToList(json: String?): List<Weather?> {
        if (json.isNullOrBlank()) return emptyList()
        return gson.fromJson(json, weatherListType)
    }
}
