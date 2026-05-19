package com.galeopsis.weatherapp.model.api

data class PairDeviceRequest(
    val pairingCode: String,
    val deviceName: String
)

data class PairDeviceResponse(
    val token: String,
    val login: String,
    val displayName: String
)
