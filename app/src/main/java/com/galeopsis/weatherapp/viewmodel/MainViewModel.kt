package com.galeopsis.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galeopsis.weatherapp.model.repository.ForecastUiItem
import com.galeopsis.weatherapp.model.repository.WeatherRepository
import com.galeopsis.weatherapp.model.repository.WeatherScreenData
import com.galeopsis.weatherapp.model.settings.SettingsRepository
import com.galeopsis.weatherapp.utils.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState.fromSettings(settingsRepository.currentSettings()))
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private var lastRequest: LastWeatherRequest = LastWeatherRequest.Coordinates(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update { currentState ->
                    currentState.copy(
                        temperatureUnit = settings.units.temperatureLabel,
                        windSpeedUnit = settings.units.windSpeedLabel
                    )
                }
            }
        }
    }

    fun loadDefaultLocation() {
        loadByCoordinates(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    }

    fun refresh() {
        when (val request = lastRequest) {
            is LastWeatherRequest.City -> loadByCity(request.cityName)
            is LastWeatherRequest.Coordinates -> loadByCoordinates(request.latitude, request.longitude)
        }
    }

    fun loadByCity(cityName: String) {
        val normalizedCityName = cityName.trim()
        if (normalizedCityName.isEmpty()) {
            emitMessage("Введите название города")
            return
        }

        lastRequest = LastWeatherRequest.City(normalizedCityName)
        viewModelScope.launch {
            loadWeather {
                weatherRepository.loadByCity(normalizedCityName)
            }
        }
    }

    fun loadByCoordinates(latitude: String, longitude: String) {
        lastRequest = LastWeatherRequest.Coordinates(latitude, longitude)
        viewModelScope.launch {
            loadWeather {
                weatherRepository.loadByCoordinates(latitude, longitude)
            }
        }
    }

    fun showError(message: String) {
        _state.update { currentState -> currentState.copy(isLoading = false) }
        emitMessage(message)
    }

    private suspend fun loadWeather(block: suspend () -> WeatherScreenData) {
        _state.update { currentState -> currentState.copy(isLoading = true) }

        runCatching { block() }
            .onSuccess { data ->
                _state.update { currentState ->
                    data.toUiState().copy(
                        isLoading = false,
                        temperatureUnit = currentState.temperatureUnit,
                        windSpeedUnit = currentState.windSpeedUnit
                    )
                }
            }
            .onFailure { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }
                _state.update { currentState -> currentState.copy(isLoading = false) }
                emitMessage(throwable.toUserMessage())
            }
    }

    private fun emitMessage(message: String) {
        _events.tryEmit(UiEvent.ShowSnackbar(message))
    }

    private fun WeatherScreenData.toUiState(): WeatherUiState {
        val settings = settingsRepository.currentSettings()
        return WeatherUiState(
            cityName = cityName,
            currentTemp = currentTemp,
            description = description,
            humidity = humidity,
            windSpeed = windSpeed,
            sunrise = sunrise,
            sunset = sunset,
            iconUrl = iconUrl,
            forecastItems = forecastItems,
            hasData = true,
            temperatureUnit = settings.units.temperatureLabel,
            windSpeedUnit = settings.units.windSpeedLabel
        )
    }

    private sealed interface LastWeatherRequest {
        data class City(val cityName: String) : LastWeatherRequest
        data class Coordinates(val latitude: String, val longitude: String) : LastWeatherRequest
    }

    companion object {
        private const val DEFAULT_LATITUDE = "56.0483"
        private const val DEFAULT_LONGITUDE = "92.9171"
    }
}

data class WeatherUiState(
    val isLoading: Boolean = false,
    val hasData: Boolean = false,
    val cityName: String = "",
    val currentTemp: String = "",
    val temperatureUnit: String = "°C",
    val description: String = "",
    val humidity: String = "",
    val windSpeed: String = "",
    val windSpeedUnit: String = "м/с",
    val sunrise: String = "",
    val sunset: String = "",
    val iconUrl: String = "",
    val forecastItems: List<ForecastUiItem> = emptyList()
) {
    companion object {
        fun fromSettings(settings: com.galeopsis.weatherapp.model.settings.AppSettings): WeatherUiState {
            return WeatherUiState(
                temperatureUnit = settings.units.temperatureLabel,
                windSpeedUnit = settings.units.windSpeedLabel
            )
        }
    }
}
