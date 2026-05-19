package com.galeopsis.weatherapp.model.api

import com.galeopsis.weatherapp.model.data.WeatherEntity
import com.galeopsis.weatherapp.model.data.forecastResponse.RResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherApi {

    @POST("api/auth/pair")
    suspend fun pairDevice(
        @Body request: PairDeviceRequest
    ): PairDeviceResponse

    @GET("api/weather/current")
    suspend fun getWeatherByCityName(
        @Query("q") cityName: String,
        @Query("units") units: String
    ): WeatherEntity

    @GET("api/weather/current")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String
    ): WeatherEntity

    @GET("api/weather/forecast")
    suspend fun getForecast(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String
    ): RResponse
}
