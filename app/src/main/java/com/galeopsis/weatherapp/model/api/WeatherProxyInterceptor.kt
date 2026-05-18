package com.galeopsis.weatherapp.model.api

import com.galeopsis.weatherapp.model.settings.SettingsRepository
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class WeatherProxyInterceptor(
    private val settingsRepository: SettingsRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val settings = settingsRepository.currentSettings()
        val serverUrl = settings.normalizedServerUrl()
        val token = settings.normalizedServerToken()

        check(serverUrl.isNotBlank()) { "Адрес сервера не задан. Укажите адрес в настройках." }
        check(token.isNotBlank()) { "Токен сервера не задан. Укажите токен в настройках." }

        val baseUrl = serverUrl.toHttpUrlOrNull()
            ?: error("Некорректный адрес сервера. Проверьте настройки.")

        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val targetUrlBuilder = baseUrl.newBuilder()

        originalUrl.encodedPathSegments
            .filter { it.isNotEmpty() }
            .forEach { segment -> targetUrlBuilder.addEncodedPathSegment(segment) }

        val targetUrl = targetUrlBuilder
            .encodedQuery(originalUrl.encodedQuery)
            .build()

        val targetRequest = originalRequest.newBuilder()
            .url(targetUrl)
            .header(HEADER_AUTH_TOKEN, token)
            .build()

        return chain.proceed(targetRequest)
    }

    private companion object {
        private const val HEADER_AUTH_TOKEN = "X-Weather-Proxy-Token"
    }
}
