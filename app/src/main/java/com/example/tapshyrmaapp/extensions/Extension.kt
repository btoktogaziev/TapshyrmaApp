package com.example.tapshyrmaapp.extensions

import android.annotation.SuppressLint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
fun Long.toFormattedDateTime(): String {
    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.systemDefault()
    )
    val formatter = DateTimeFormatter.ofPattern("MMMM, dd\nHH:mm")
    return dateTime.format(formatter)
}
