package com.galeopsis.weatherapp.model.settings

data class AppSettings(
    val apiKey: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val units: WeatherUnits = WeatherUnits.METRIC
) {
    fun effectiveApiKey(defaultApiKey: String): String {
        return apiKey.trim().ifBlank { defaultApiKey.trim() }
    }
}

enum class ThemeMode(
    val title: String
) {
    SYSTEM("Как в системе"),
    LIGHT("Светлая"),
    DARK("Тёмная")
}

enum class WeatherUnits(
    val title: String,
    val apiValue: String,
    val temperatureLabel: String,
    val windSpeedLabel: String
) {
    METRIC(
        title = "Метрические",
        apiValue = "metric",
        temperatureLabel = "°C",
        windSpeedLabel = "м/с"
    ),
    IMPERIAL(
        title = "Имперские",
        apiValue = "imperial",
        temperatureLabel = "°F",
        windSpeedLabel = "mph"
    )
}
