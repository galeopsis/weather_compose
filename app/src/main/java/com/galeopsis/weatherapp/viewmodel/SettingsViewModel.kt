package com.galeopsis.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galeopsis.weatherapp.model.api.PairDeviceRequest
import com.galeopsis.weatherapp.model.api.WeatherApi
import com.galeopsis.weatherapp.model.settings.SettingsRepository
import com.galeopsis.weatherapp.model.settings.ThemeMode
import com.galeopsis.weatherapp.model.settings.WeatherUnits
import com.galeopsis.weatherapp.utils.toUserMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val weatherApi: WeatherApi
) : ViewModel() {

    val settings = settingsRepository.settings

    private val _isPairing = MutableStateFlow(false)
    val isPairing: StateFlow<Boolean> = _isPairing.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun pairDevice(serverUrl: String, pairingCode: String, deviceName: String) {
        val normalizedServerUrl = serverUrl.trim()
        val normalizedPairingCode = pairingCode.trim()
        val normalizedDeviceName = deviceName.trim().ifBlank { "Android" }

        if (normalizedServerUrl.isBlank()) {
            _events.tryEmit(UiEvent.ShowSnackbar("Укажите адрес сервера"))
            return
        }

        if (normalizedPairingCode.isBlank()) {
            _events.tryEmit(UiEvent.ShowSnackbar("Укажите код привязки"))
            return
        }

        viewModelScope.launch {
            _isPairing.value = true
            try {
                settingsRepository.saveServerSettings(
                    serverUrl = normalizedServerUrl,
                    serverToken = settingsRepository.currentSettings().serverToken
                )

                val response = weatherApi.pairDevice(
                    PairDeviceRequest(
                        pairingCode = normalizedPairingCode,
                        deviceName = normalizedDeviceName
                    )
                )

                settingsRepository.saveServerSettings(
                    serverUrl = normalizedServerUrl,
                    serverToken = response.token
                )
                _events.emit(UiEvent.ShowSnackbar("Устройство привязано"))
            } catch (throwable: Throwable) {
                _events.emit(UiEvent.ShowSnackbar(throwable.toUserMessage()))
            } finally {
                _isPairing.value = false
            }
        }
    }

    fun saveServerSettings(serverUrl: String, serverToken: String) {
        settingsRepository.saveServerSettings(serverUrl, serverToken)
        _events.tryEmit(UiEvent.ShowSnackbar("Настройки сервера сохранены"))
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
