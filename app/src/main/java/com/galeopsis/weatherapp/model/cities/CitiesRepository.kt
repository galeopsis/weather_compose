package com.galeopsis.weatherapp.model.cities

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray

class CitiesRepository(application: Application) {

    private val preferences = application.getSharedPreferences(PREFERENCES_NAME, 0)
    private val _cities = MutableStateFlow(readCities())
    val cities: StateFlow<List<String>> = _cities.asStateFlow()

    fun addCity(cityName: String) {
        val normalizedCityName = cityName.trim()
        if (normalizedCityName.isBlank()) return

        val updatedCities = buildList {
            add(normalizedCityName)
            addAll(_cities.value.filterNot { it.equals(normalizedCityName, ignoreCase = true) })
        }.take(MAX_CITIES)

        saveCities(updatedCities)
    }

    fun removeCity(cityName: String) {
        val updatedCities = _cities.value.filterNot { it.equals(cityName, ignoreCase = true) }
        saveCities(updatedCities)
    }

    fun clearCities() {
        saveCities(emptyList())
    }

    private fun saveCities(cities: List<String>) {
        preferences.edit()
            .putString(KEY_CITIES, JSONArray(cities).toString())
            .apply()
        _cities.value = cities
    }

    private fun readCities(): List<String> {
        val rawValue = preferences.getString(KEY_CITIES, null) ?: return emptyList()
        return runCatching {
            val jsonArray = JSONArray(rawValue)
            List(jsonArray.length()) { index -> jsonArray.getString(index) }
                .filter { it.isNotBlank() }
                .distinctBy { it.lowercase() }
        }.getOrDefault(emptyList())
    }

    private companion object {
        private const val PREFERENCES_NAME = "weather_cities"
        private const val KEY_CITIES = "cities"
        private const val MAX_CITIES = 30
    }
}
