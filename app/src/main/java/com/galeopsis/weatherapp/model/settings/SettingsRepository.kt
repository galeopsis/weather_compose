package com.galeopsis.weatherapp.model.settings

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(application: Application) {

    private val preferences = application.getSharedPreferences(PREFERENCES_NAME, 0)
    private val _settings = MutableStateFlow(readSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    fun saveServerUrl(serverUrl: String) {
        preferences.edit()
            .putString(KEY_SERVER_URL, serverUrl.trim())
            .apply()
        refresh()
    }

    fun saveServerSettings(serverUrl: String, serverToken: String) {
        preferences.edit()
            .putString(KEY_SERVER_URL, serverUrl.trim())
            .putString(KEY_SERVER_TOKEN, serverToken.trim())
            .apply()
        refresh()
    }

    fun saveDeviceToken(serverToken: String) {
        preferences.edit()
            .putString(KEY_SERVER_TOKEN, serverToken.trim())
            .apply()
        refresh()
    }

    fun saveThemeMode(themeMode: ThemeMode) {
        preferences.edit()
            .putString(KEY_THEME_MODE, themeMode.name)
            .apply()
        refresh()
    }

    fun saveUnits(units: WeatherUnits) {
        preferences.edit()
            .putString(KEY_UNITS, units.name)
            .apply()
        refresh()
    }

    fun currentSettings(): AppSettings {
        return _settings.value
    }

    private fun refresh() {
        _settings.value = readSettings()
    }

    private fun readSettings(): AppSettings {
        return AppSettings(
            serverUrl = preferences.getString(KEY_SERVER_URL, "").orEmpty(),
            serverToken = preferences.getString(KEY_SERVER_TOKEN, "").orEmpty(),
            themeMode = preferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name).toEnumOrDefault(ThemeMode.SYSTEM),
            units = preferences.getString(KEY_UNITS, WeatherUnits.METRIC.name).toEnumOrDefault(WeatherUnits.METRIC)
        )
    }

    private inline fun <reified T : Enum<T>> String?.toEnumOrDefault(defaultValue: T): T {
        val rawValue = this ?: return defaultValue
        return runCatching { enumValueOf<T>(rawValue) }.getOrDefault(defaultValue)
    }

    private companion object {
        private const val PREFERENCES_NAME = "weather_settings"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_SERVER_TOKEN = "server_token"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_UNITS = "units"
    }
}
