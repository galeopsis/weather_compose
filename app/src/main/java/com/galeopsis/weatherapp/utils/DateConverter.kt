package com.galeopsis.weatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs

fun Int.unixTimestampToTimeString(zone: Int): String {
    return try {
        val sign = if (zone >= 0) "+" else "-"
        val absoluteZone = abs(zone)
        val hours = absoluteZone / 3600
        val minutes = (absoluteZone % 3600) / 60
        val outputDateFormat = SimpleDateFormat("HH:mm:ss", Locale.ROOT)
        outputDateFormat.timeZone = TimeZone.getTimeZone("GMT$sign%02d:%02d".format(hours, minutes))
        outputDateFormat.format(Date(this * 1000L))
    } catch (e: Exception) {
        this.toString()
    }
}
