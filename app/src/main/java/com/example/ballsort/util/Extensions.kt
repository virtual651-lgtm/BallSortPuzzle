/*
 * com.example.ballsort.util
 * Süre biçimlendirme ve API 24+ uyumlu tarih yardımcıları.
 */
package com.example.ballsort.util

import java.util.Calendar
import java.util.Locale

fun formatDuration(elapsedMs: Long): String {
    val totalSeconds = (elapsedMs / 1_000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

fun currentEpochDay(): Long {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
    return year * 366L + dayOfYear
}

