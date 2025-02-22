package com.example.newspulse.utils

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH) // Your date format
    val outputFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.ENGLISH) // Desired format

    return try {
        val date: Date = inputFormat.parse(inputDate) ?: return "Unknown Date"
        outputFormat.format(date)
    } catch (e: Exception) {
        "Invalid Date"
    }
}