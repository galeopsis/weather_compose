package com.galeopsis.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import com.galeopsis.weatherapp.model.settings.SettingsRepository
import com.galeopsis.weatherapp.model.settings.ThemeMode
import com.galeopsis.weatherapp.model.settings.WeatherUnits
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings = settingsRepository.settings

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun saveApiKey(apiKey: String) {
        settingsRepository.saveApiKey(apiKey)
        _events.tryEmit(UiEvent.ShowSnackbar("API-ключ сохранён"))
    }

    fun saveThemeMode(themeMode: ThemeMode) {
        settingsRepository.saveThemeMode(themeMode)
        _events.tryEmit(UiEvent.ShowSnackbar("Тема изменена"))
    }

    fun saveUnits(units: WeatherUnits) {
        settingsRepository.saveUnits(units)
        _events.tryEmit(UiEvent.ShowSnackbar("Единицы измерения изменены"))
    }
}
