package com.devstudio.receipto.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object CommonDateFormatter {
    fun formatTimestampToString(timestamp: Long, timeZone: TimeZone = TimeZone.UTC): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(timeZone)
        return "${dateTime.year.toString().padStart(4, '0')}-${dateTime.monthNumber.toString().padStart(2, '0')}-${dateTime.dayOfMonth.toString().padStart(2, '0')}"
    }

    fun parseDateStringToTimestamp(dateString: String?, timeZone: TimeZone = TimeZone.UTC): Long? {
        if (dateString.isNullOrBlank()) return null
        return try {
            val parts = dateString.split("-")
            if (parts.size == 3) {
                val year = parts[0].toInt()
                val month = parts[1].toInt()
                val day = parts[2].toInt()
                LocalDate(year, month, day).atStartOfDayIn(timeZone).toEpochMilliseconds()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
