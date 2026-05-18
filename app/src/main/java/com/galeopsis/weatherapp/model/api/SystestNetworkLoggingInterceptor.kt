package com.galeopsis.weatherapp.model.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset

class SystestNetworkLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url.toString()

        logRequest(request, requestUrl)

        return try {
            val response = chain.proceed(request)
            logResponse(requestUrl, response)
            response
        } catch (throwable: IOException) {
            logError(requestUrl, throwable)
            throw throwable
        }
    }

    private fun logRequest(request: Request, requestUrl: String) {
        Log.d(
            TAG,
            "->  request [$APP_NAME]: [$requestUrl] [${request.body.asLogText()}]"
        )
    }

    private fun logResponse(requestUrl: String, response: Response) {
        Log.d(
            TAG,
            "<- response [$APP_NAME]: [$requestUrl] [${response.code}] [${response.message}]"
        )
    }

    private fun logError(requestUrl: String, throwable: IOException) {
        val errorName = throwable::class.java.simpleName.ifBlank { "IOException" }
        val errorMessage = throwable.message.orEmpty()
        val normalizedMessage = if (errorMessage.isBlank()) errorName else "$errorName: $errorMessage"

        Log.d(
            TAG,
            "<- response [$APP_NAME]: [$requestUrl] [error] [$normalizedMessage]"
        )
    }

    private fun RequestBody?.asLogText(): String {
        val body = this ?: return NULL_BODY

        if (body.isDuplex() || body.isOneShot()) {
            return NULL_BODY
        }

        return runCatching {
            val buffer = Buffer()
            body.writeTo(buffer)

            val charset = body.contentType()?.charset(DEFAULT_CHARSET) ?: DEFAULT_CHARSET
            buffer.readString(charset).ifBlank { NULL_BODY }
        }.getOrDefault(NULL_BODY)
    }

    private companion object {
        private const val TAG = "systest"
        private const val APP_NAME = "WeatherApp"
        private const val NULL_BODY = "null"
        private val DEFAULT_CHARSET: Charset = Charsets.UTF_8
    }
}
