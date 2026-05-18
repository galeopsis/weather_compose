package com.galeopsis.weatherapp.utils

import com.google.gson.JsonParseException
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserMessage(): String {
    return when (this) {
        is IllegalStateException -> message.takeIf { !it.isNullOrBlank() } ?: DEFAULT_ERROR_MESSAGE
        is IllegalArgumentException -> message.takeIf { !it.isNullOrBlank() } ?: DEFAULT_ERROR_MESSAGE
        is UnknownHostException -> "Сервер погоды недоступен. Проверьте подключение к интернету."
        is ConnectException -> "Сервер погоды недоступен. Проверьте адрес сервера."
        is SocketTimeoutException -> "Сервер погоды не отвечает. Повторите попытку позже."
        is JsonParseException -> "Сервер погоды вернул некорректные данные."
        is HttpException -> toHttpUserMessage()
        else -> DEFAULT_ERROR_MESSAGE
    }
}

private fun HttpException.toHttpUserMessage(): String {
    val serverMessage = readProblemTitle()
    if (!serverMessage.isNullOrBlank()) {
        return serverMessage
    }

    return when (code()) {
        401 -> "Неверный токен сервера. Проверьте настройки."
        403 -> "Доступ к серверу запрещён."
        404 -> "Город не найден."
        408 -> "Сервер погоды не отвечает. Повторите попытку позже."
        429 -> "Слишком много запросов. Повторите попытку позже."
        in 500..599 -> "Сервис погоды временно недоступен."
        else -> DEFAULT_ERROR_MESSAGE
    }
}

private fun HttpException.readProblemTitle(): String? {
    return runCatching {
        val body = response()?.errorBody()?.string().orEmpty()
        if (body.isBlank()) return@runCatching null
        JSONObject(body).optString("title").takeIf { it.isNotBlank() }
    }.getOrNull()
}

private const val DEFAULT_ERROR_MESSAGE = "Не удалось загрузить погоду"
