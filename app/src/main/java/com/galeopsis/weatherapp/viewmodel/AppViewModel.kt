package com.galeopsis.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import com.galeopsis.weatherapp.model.settings.SettingsRepository

class AppViewModel(
    settingsRepository: SettingsRepository
) : ViewModel() {
    val settings = settingsRepository.settings
}
