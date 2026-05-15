package com.galeopsis.weatherapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.galeopsis.weatherapp.R
import com.galeopsis.weatherapp.ui.about.AboutRoute
import com.galeopsis.weatherapp.ui.cities.CitiesRoute
import com.galeopsis.weatherapp.ui.navigation.AppRoute
import com.galeopsis.weatherapp.ui.settings.SettingsRoute
import com.galeopsis.weatherapp.ui.theme.WeatherTheme
import com.galeopsis.weatherapp.viewmodel.AppViewModel
import com.galeopsis.weatherapp.viewmodel.CitiesViewModel
import com.galeopsis.weatherapp.viewmodel.MainViewModel
import com.galeopsis.weatherapp.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WeatherApp(
    appViewModel: AppViewModel,
    mainViewModel: MainViewModel,
    versionName: String,
    onLocationClick: () -> Unit
) {
    val settings by appViewModel.settings.collectAsStateWithLifecycle()

    WeatherTheme(themeMode = settings.themeMode) {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                AppBottomNavigationBar(
                    currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route,
                    onRouteClick = { route ->
                        navController.navigate(route.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background_weather_new),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xAA000000),
                                    Color(0x44000000),
                                    Color(0xCC000000)
                                )
                            )
                        )
                )

                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Weather.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(AppRoute.Weather.route) {
                        WeatherRoute(
                            viewModel = mainViewModel,
                            versionName = versionName,
                            snackbarHostState = snackbarHostState,
                            onLocationClick = onLocationClick
                        )
                    }
                    composable(AppRoute.Cities.route) {
                        val citiesViewModel: CitiesViewModel = koinViewModel()
                        CitiesRoute(
                            viewModel = citiesViewModel,
                            snackbarHostState = snackbarHostState,
                            onCityClick = { cityName ->
                                mainViewModel.loadByCity(cityName)
                                navController.navigate(AppRoute.Weather.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                    composable(AppRoute.Settings.route) {
                        val settingsViewModel: SettingsViewModel = koinViewModel()
                        SettingsRoute(
                            viewModel = settingsViewModel,
                            snackbarHostState = snackbarHostState
                        )
                    }
                    composable(AppRoute.About.route) {
                        AboutRoute(versionName = versionName)
                    }
                }
            }
        }
    }
}

@Composable
internal fun AppBottomNavigationBar(
    currentRoute: String?,
    onRouteClick: (AppRoute) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xE6000000)
    ) {
        AppRoute.bottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    onRouteClick(item)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.bottomTitle,
                        maxLines = 1,
                        softWrap = false,
                        fontSize = 11.sp
                    )
                }
            )
        }
    }
}
