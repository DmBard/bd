package com.dmbaryshev.bd.utils

import java.text.SimpleDateFormat
import java.util.*

private val TIME_FORMAT = "dd.MM.yyy HH:mm"

fun convertTimestampToString(timestamp: Long?): String {
    val formatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = if (timestamp != null) (timestamp * 1000).toLong() else return ""
    return formatter.format(calendar.time)
}

fun Calendar.getInterval(): Long {
    val now = Calendar.getInstance()
    val koeff = if (!after(now)) now.get(Calendar.DAY_OF_YEAR) else 1
    return Math.abs(timeInMillis * koeff - now.timeInMillis);
}

fun now(): Long {
    return Calendar.getInstance().timeInMillis
}
