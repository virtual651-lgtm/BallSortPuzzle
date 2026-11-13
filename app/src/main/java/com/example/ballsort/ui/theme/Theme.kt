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
    primary = RoyalLightButton,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = RoyalLightEmeraldText,
    secondary = Color(0xFF388E3C),
    onSecondary = Color.White,
    background = RoyalLightBg,
    surface = RoyalLightSurface,
    surfaceVariant = RoyalLightCard,
    onSurface = RoyalLightEmeraldText,
    onSurfaceVariant = Color(0xFF2E7D32),
)

private val DarkColors = darkColorScheme(
    primary = RoyalDarkButton,
    onPrimary = Color(0xFF1E1400),
    primaryContainer = Color(0xFFFFA000),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF4F8DFF),
    onSecondary = Color.White,
    background = RoyalDarkBg,
    surface = RoyalDarkSurface,
    surfaceVariant = RoyalDarkCard,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFA6B0DE),
)

@Composable
fun BallSortTheme(
    themeMode: String = "system",
    dynamicColor: Boolean = false,
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

