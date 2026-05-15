package com.galeopsis.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.galeopsis.weatherapp.ui.WeatherApp
import com.galeopsis.weatherapp.viewmodel.AppViewModel
import com.galeopsis.weatherapp.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val appViewModel by viewModel<AppViewModel>()
    private val mainViewModel by viewModel<MainViewModel>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val isGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            loadCurrentLocationOrDefault()
        } else {
            mainViewModel.showError("Разрешение на геолокацию не выдано. Показана погода для Красноярска.")
            mainViewModel.loadDefaultLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyOrientationPolicy()
        configureEdgeToEdge()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            WeatherApp(
                appViewModel = appViewModel,
                mainViewModel = mainViewModel,
                versionName = BuildConfig.VERSION_NAME,
                onLocationClick = ::requestLocationOrDefault
            )
        }

        if (savedInstanceState == null) {
            requestLocationOrDefault()
        }
    }

    private fun configureEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun applyOrientationPolicy() {
        requestedOrientation = if (resources.configuration.smallestScreenWidthDp >= 600) {
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun requestLocationOrDefault() {
        when {
            hasLocationPermission() && isLocationEnabled() -> loadCurrentLocationOrDefault()
            hasLocationPermission() && !isLocationEnabled() -> {
                mainViewModel.showError("Геолокация отключена. Показана погода для Красноярска.")
                mainViewModel.loadDefaultLocation()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            else -> locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadCurrentLocationOrDefault() {
        if (!hasLocationPermission()) {
            mainViewModel.loadDefaultLocation()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    mainViewModel.loadByCoordinates(
                        latitude = location.latitude.toString(),
                        longitude = location.longitude.toString()
                    )
                } else {
                    mainViewModel.showError("Последняя геопозиция недоступна. Показана погода для Красноярска.")
                    mainViewModel.loadDefaultLocation()
                }
            }
            .addOnFailureListener {
                mainViewModel.showError("Не удалось получить геопозицию. Показана погода для Красноярска.")
                mainViewModel.loadDefaultLocation()
            }
    }

    private fun hasLocationPermission(): Boolean {
        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return coarseLocationGranted || fineLocationGranted
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
