package com.galeopsis.weatherapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AppRoute(
    val route: String,
    val title: String,
    val bottomTitle: String,
    val icon: ImageVector
) {

    object Weather : AppRoute(
        route = "weather",
        title = "Погода",
        bottomTitle = "Погода",
        icon = Icons.Filled.WbSunny
    )

    object Cities : AppRoute(
        route = "cities",
        title = "Города",
        bottomTitle = "Города",
        icon = Icons.Filled.LocationCity
    )

    object Settings : AppRoute(
        route = "settings",
        title = "Настройки",
        bottomTitle = "Настройки",
        icon = Icons.Filled.Settings
    )

    object About : AppRoute(
        route = "about",
        title = "О приложении",
        bottomTitle = "Инфо",
        icon = Icons.Filled.Info
    )

    companion object {

        val bottomItems: List<AppRoute>
            get() = listOf(
                Weather,
                Cities,
                Settings,
                About
            )
    }
}