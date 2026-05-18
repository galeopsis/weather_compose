package com.galeopsis.weatherapp.model.repository

import com.galeopsis.weatherapp.model.api.WeatherApi
import com.galeopsis.weatherapp.model.cities.CitiesRepository
import com.galeopsis.weatherapp.model.dao.WeatherDao
import com.galeopsis.weatherapp.model.data.WeatherEntity
import com.galeopsis.weatherapp.model.data.forecastResponse.RList
import com.galeopsis.weatherapp.model.settings.SettingsRepository
import com.galeopsis.weatherapp.utils.unixTimestampToTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class WeatherRepository(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao,
    private val settingsRepository: SettingsRepository,
    private val citiesRepository: CitiesRepository
) {

    val cachedWeather: Flow<List<WeatherEntity>> = weatherDao.observeAll()

    suspend fun loadByCity(cityName: String): WeatherScreenData {
        return withContext(Dispatchers.IO) {
            val requestParams = getRequestParams()
            val current = weatherApi.getWeatherByCityName(
                cityName = cityName.trim(),
                units = requestParams.units
            )
            saveCurrentWeather(current)
            val screenData = loadForecastForCurrentWeather(current, requestParams)
            citiesRepository.addCity(screenData.cityName)
            screenData
        }
    }

    suspend fun loadByCoordinates(latitude: String, longitude: String): WeatherScreenData {
        return withContext(Dispatchers.IO) {
            val requestParams = getRequestParams()
            val current = weatherApi.getWeatherByCoordinates(
                latitude = latitude,
                longitude = longitude,
                units = requestParams.units
            )
            saveCurrentWeather(current)
            loadForecastForCurrentWeather(current, requestParams)
        }
    }

    private fun getRequestParams(): RequestParams {
        val settings = settingsRepository.currentSettings()
        val serverUrl = settings.normalizedServerUrl()
        val serverToken = settings.normalizedServerToken()

        check(serverUrl.isNotBlank()) { "Адрес сервера не задан. Укажите адрес в настройках." }
        check(serverUrl.toHttpUrlOrNull() != null) { "Некорректный адрес сервера. Проверьте настройки." }
        check(serverToken.isNotBlank()) { "Токен сервера не задан. Укажите токен в настройках." }

        return RequestParams(
            units = settings.units.apiValue
        )
    }

    private suspend fun saveCurrentWeather(current: WeatherEntity) {
        weatherDao.deleteAllData()
        weatherDao.add(current)
    }

    private suspend fun loadForecastForCurrentWeather(
        current: WeatherEntity,
        requestParams: RequestParams
    ): WeatherScreenData {
        val coord = current.coord
        val forecastItems = if (coord != null) {
            val response = weatherApi.getForecast(
                latitude = coord.lat.toString(),
                longitude = coord.lon.toString(),
                units = requestParams.units
            )
            buildForecastItems(response.list)
        } else {
            emptyList()
        }

        return current.toScreenData(forecastItems)
    }

    private fun WeatherEntity.toScreenData(forecastItems: List<ForecastUiItem>): WeatherScreenData {
        val weatherInfo = weather.firstOrNull()
        val normalizedCityName = when (name) {
            "Бадалык" -> "Красноярск"
            null, "" -> "Неизвестный город"
            else -> name.orEmpty()
        }

        return WeatherScreenData(
            cityName = normalizedCityName,
            currentTemp = main?.temp?.roundToInt()?.toString().orEmpty(),
            description = weatherInfo?.description.orEmpty(),
            humidity = main?.humidity?.toString().orEmpty(),
            windSpeed = wind?.speed?.roundToInt()?.toString().orEmpty(),
            sunrise = timezone?.let { sys?.sunrise?.unixTimestampToTimeString(it) }.orEmpty(),
            sunset = timezone?.let { sys?.sunset?.unixTimestampToTimeString(it) }.orEmpty(),
            iconUrl = weatherInfo?.icon?.toWeatherIconUrl().orEmpty(),
            forecastItems = forecastItems
        )
    }

    private fun buildForecastItems(items: List<RList>): List<ForecastUiItem> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        val labelFormat = SimpleDateFormat("d MMMM", Locale("ru"))
        val today = dateFormat.format(Calendar.getInstance().time)

        return items
            .asSequence()
            .filter { it.dtTxt.substringBefore(" ") > today }
            .groupBy { it.dtTxt.substringBefore(" ") }
            .toSortedMap()
            .entries
            .take(3)
            .map { (date, dayItems) ->
                val representativeItem = dayItems.minByOrNull { abs(it.hourOfDay() - 12) } ?: dayItems.first()
                val dateLabel = runCatching {
                    val parsedDate = dateFormat.parse(date)
                    if (parsedDate != null) {
                        val calendar = Calendar.getInstance().apply { time = parsedDate }
                        val dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale("ru")).orEmpty()
                        "${dayOfWeek.lowercase(Locale("ru"))}, ${labelFormat.format(parsedDate)}"
                    } else {
                        date
                    }
                }.getOrDefault(date)

                ForecastUiItem(
                    dateLabel = dateLabel,
                    temperature = dayItems.mapNotNull { it.main.temp?.roundToInt() }.maxOrNull()?.toString().orEmpty(),
                    description = representativeItem.weather.firstOrNull()?.description.orEmpty(),
                    iconUrl = representativeItem.weather.firstOrNull()?.icon?.toWeatherIconUrl().orEmpty()
                )
            }
    }

    private fun RList.hourOfDay(): Int {
        return dtTxt.substringAfter(" ").substringBefore(":").toIntOrNull() ?: 12
    }

    private fun String.toWeatherIconUrl(): String {
        return "https://openweathermap.org/img/w/$this.png"
    }

    private data class RequestParams(
        val units: String
    )
}

data class WeatherScreenData(
    val cityName: String,
    val currentTemp: String,
    val description: String,
    val humidity: String,
    val windSpeed: String,
    val sunrise: String,
    val sunset: String,
    val iconUrl: String,
    val forecastItems: List<ForecastUiItem>
)

data class ForecastUiItem(
    val dateLabel: String,
    val temperature: String,
    val description: String,
    val iconUrl: String
)
