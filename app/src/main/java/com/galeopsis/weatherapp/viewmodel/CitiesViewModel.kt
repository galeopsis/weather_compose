package com.galeopsis.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import com.galeopsis.weatherapp.model.cities.CitiesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class CitiesViewModel(
    private val citiesRepository: CitiesRepository
) : ViewModel() {

    val cities = citiesRepository.cities

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun removeCity(cityName: String) {
        citiesRepository.removeCity(cityName)
        _events.tryEmit(UiEvent.ShowSnackbar("Город удалён"))
    }

    fun clearCities() {
        citiesRepository.clearCities()
        _events.tryEmit(UiEvent.ShowSnackbar("Список городов очищен"))
    }
}
