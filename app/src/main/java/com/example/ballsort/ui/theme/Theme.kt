/*
 * com.example.ballsort.ui.theme
 * Açık / Koyu / Sistem ve Android 12+ Dinamik Renk destekli Material 3 teması.
 */
package com.example.ballsort.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = SeedPrimaryLight,
    secondary = SeedSecondaryLight,
    tertiary = SeedTertiaryLight,
    background = Color(0xFFF6F7FB),
    surface = Color(0xFFFFFFFF),
)

private val DarkColors = darkColorScheme(
    primary = SeedPrimaryDark,
    secondary = SeedSecondaryDark,
    tertiary = SeedTertiaryDark,
    background = Color(0xFF13131A),
    surface = Color(0xFF1B1C26),
)

@Composable
fun BallSortTheme(
    themeMode: String = "system",
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BallSortTypography,
        content = content,
    )
}

