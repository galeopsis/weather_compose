package com.galeopsis.weatherapp.model.settings

data class AppSettings(
    val serverUrl: String = "",
    val serverToken: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val units: WeatherUnits = WeatherUnits.METRIC
) {
    fun normalizedServerUrl(): String {
        val trimmedUrl = serverUrl.trim()
        if (trimmedUrl.isEmpty()) return ""

        val withScheme = if (trimmedUrl.contains("://")) {
            trimmedUrl
        } else {
            "http://$trimmedUrl"
        }

        return if (withScheme.endsWith("/")) withScheme else "$withScheme/"
    }

    fun normalizedServerToken(): String {
        return serverToken.trim()
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
