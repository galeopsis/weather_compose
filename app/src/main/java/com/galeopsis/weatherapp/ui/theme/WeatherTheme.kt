package com.galeopsis.weatherapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.galeopsis.weatherapp.model.settings.ThemeMode

private val DarkWeatherColorScheme = darkColorScheme(
    primary = Color(0xFF0FECD7),
    onPrimary = Color(0xFF00201C),
    secondary = Color(0xFF81C784),
    onSecondary = Color(0xFF00210B),
    surface = Color(0xCC000000),
    onSurface = Color.White,
    background = Color(0xFF08111D),
    onBackground = Color.White,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightWeatherColorScheme = lightColorScheme(
    primary = Color(0xFF006B5D),
    onPrimary = Color.White,
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    surface = Color(0xF2FFFFFF),
    onSurface = Color(0xFF102030),
    background = Color(0xFFEAF5FF),
    onBackground = Color(0xFF102030),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun WeatherTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkWeatherColorScheme else LightWeatherColorScheme,
        content = content
    )
}
