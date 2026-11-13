/*
 * com.example.ballsort
 * Ana aktivite: SplashScreen, edge-to-edge, tema ve AppCompat dil desteği.
 */
package com.example.ballsort

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ballsort.data.Settings
import com.example.ballsort.data.SettingsRepository
import com.example.ballsort.platform.LocaleHelper
import com.example.ballsort.ui.navigation.AppNavHost
import com.example.ballsort.ui.theme.BallSortTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsState by settingsRepository.settings.collectAsStateWithLifecycle(
                initialValue = null
            )
            val settings = settingsState ?: return@setContent

            LaunchedEffect(settings.language) {
                LocaleHelper.applyLanguage(settings.language)
            }
            BallSortTheme(
                themeMode = settings.theme,
                dynamicColor = settings.dynamicColor,
            ) {
                AppNavHost()
            }
        }
    }
}

