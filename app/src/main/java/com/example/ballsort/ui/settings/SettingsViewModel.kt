/*
 * com.example.ballsort.ui.settings
 * Ayarlar ekranı ViewModel'i: ses, titreşim, tema, dinamik renk, renk körlüğü, çoklu taşıma ve dil.
 */
package com.example.ballsort.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballsort.data.Settings
import com.example.ballsort.data.SettingsRepository
import com.example.ballsort.platform.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val settings: StateFlow<Settings> = settingsRepository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = Settings(),
    )

    fun setSound(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSettings { it.copy(sound = enabled) } }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSettings { it.copy(vibration = enabled) } }
    }

    fun setMultiMove(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSettings { it.copy(multiMove = enabled) } }
    }

    fun setColorBlind(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSettings { it.copy(colorBlind = enabled) } }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.updateSettings { it.copy(dynamicColor = enabled) } }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch { settingsRepository.updateSettings { it.copy(theme = theme) } }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(language = language) }
            LocaleHelper.applyLanguage(language)
        }
    }
}

