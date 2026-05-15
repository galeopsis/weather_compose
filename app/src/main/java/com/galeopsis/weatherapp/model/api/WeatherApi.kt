package com.galeopsis.weatherapp.model.api

import com.galeopsis.weatherapp.model.data.WeatherEntity
import com.galeopsis.weatherapp.model.data.forecastResponse.RResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather?lang=ru")
    suspend fun getWeatherByCityName(
        @Query("appid") appId: String,
        @Query("q") cityName: String,
        @Query("units") units: String
    ): WeatherEntity

    @GET("weather?lang=ru")
    suspend fun getWeatherByCoordinates(
        @Query("appid") appId: String,
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String
    ): WeatherEntity

    @GET("forecast?lang=ru")
    suspend fun getForecast(
        @Query("appid") appId: String,
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("units") units: String
    ): RResponse
}
