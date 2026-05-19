package com.galeopsis.weatherapp.model.api

import com.galeopsis.weatherapp.model.settings.SettingsRepository
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class WeatherProxyInterceptor(
    private val settingsRepository: SettingsRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val settings = settingsRepository.currentSettings()
        val serverUrl = settings.normalizedServerUrl()
        val token = settings.normalizedServerToken()

        check(serverUrl.isNotBlank()) { "Адрес сервера не задан. Укажите адрес в настройках." }

        val baseUrl = serverUrl.toHttpUrlOrNull()
            ?: error("Некорректный адрес сервера. Проверьте настройки.")

        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val isPairingRequest = originalUrl.encodedPath == PAIRING_PATH

        if (!isPairingRequest) {
            check(token.isNotBlank()) { "Устройство не привязано. Выполните привязку в настройках." }
        }

        val targetUrlBuilder = baseUrl.newBuilder()

        originalUrl.encodedPathSegments
            .filter { it.isNotEmpty() }
            .forEach { segment -> targetUrlBuilder.addEncodedPathSegment(segment) }

        val targetUrl = targetUrlBuilder
            .encodedQuery(originalUrl.encodedQuery)
            .build()

        val targetRequestBuilder = originalRequest.newBuilder()
            .url(targetUrl)

        if (!isPairingRequest) {
            targetRequestBuilder.header(HEADER_AUTHORIZATION, "$BEARER_PREFIX $token")
        }

        return chain.proceed(targetRequestBuilder.build())
    }

    private companion object {
        private const val PAIRING_PATH = "/api/auth/pair"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val BEARER_PREFIX = "Bearer"
    }
}
