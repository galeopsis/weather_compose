package com.galeopsis.weatherapp.utils

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.toUserMessage(): String {
    return findUserMessage() ?: DEFAULT_ERROR_MESSAGE
}

private fun Throwable.findUserMessage(): String? {
    var current: Throwable? = this
    while (current != null) {
        val message = current.directUserMessage()
        if (message != null) {
            return message
        }
        current = current.cause?.takeIf { it !== current }
    }
    return null
}

private fun Throwable.directUserMessage(): String? {
    return when (this) {
        is HttpException -> toHttpUserMessage()
        is UnknownHostException -> "Нет подключения к интернету"
        is SocketTimeoutException -> "Сервис погоды не отвечает"
        is ConnectException -> "Сервис погоды недоступен"
        is InterruptedIOException -> "Сервис погоды не отвечает"
        is JsonParseException -> "Сервис погоды вернул некорректные данные"
        is IOException -> "Не удалось подключиться к сервису погоды"
        is IllegalStateException -> toStateUserMessage()
        else -> null
    }
}

private fun HttpException.toHttpUserMessage(): String {
    return when (code()) {
        401, 403 -> "Некорректный API-ключ. Проверьте ключ в настройках"
        404 -> "Город не найден"
        429 -> "Превышен лимит запросов. Повторите попытку позже"
        in 500..599 -> "Сервис погоды временно недоступен"
        else -> DEFAULT_ERROR_MESSAGE
    }
}

private fun IllegalStateException.toStateUserMessage(): String? {
    val rawMessage = message.orEmpty()
    return when {
        rawMessage.contains("API-ключ", ignoreCase = true) -> "API-ключ не задан. Укажите ключ в настройках"
        else -> null
    }
}

private const val DEFAULT_ERROR_MESSAGE = "Не удалось загрузить погоду"
