/*
 * com.example.ballsort.platform
 * Uygulama içi dil seçimini (TR / EN / Sistem) AppCompatDelegate ile uygular.
 */
package com.example.ballsort.platform

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {
    fun applyLanguage(languageTag: String) {
        val locales = when (languageTag) {
            "tr" -> LocaleListCompat.forLanguageTags("tr")
            "en" -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.getEmptyLocaleList()
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}

