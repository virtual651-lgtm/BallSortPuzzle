/*
 * com.example.ballsort.platform
 * Uygulama içi dil seçimini (TR / EN / Sistem) AppCompatDelegate ile uygular.
 */
package com.example.ballsort.platform

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {
    fun applyLanguage(languageTag: String) {
        val targetLocales = when (languageTag) {
            "tr" -> LocaleListCompat.forLanguageTags("tr")
            "en" -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.getEmptyLocaleList()
        }
        val currentLocales = AppCompatDelegate.getApplicationLocales()
        if (currentLocales.toLanguageTags() != targetLocales.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(targetLocales)
        }
    }
}

