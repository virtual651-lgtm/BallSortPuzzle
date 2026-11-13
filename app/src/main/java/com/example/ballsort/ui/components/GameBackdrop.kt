/*
 * com.example.ballsort.ui.components
 * Oyuna özgü gradyan ve yumuşak ışık parıltılı sahne arka planı.
 */
package com.example.ballsort.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

import androidx.compose.ui.graphics.Path

@Composable
fun GameBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val gradientColors = if (isDark) {
        listOf(
            Color(0xFF0C1033),
            Color(0xFF141847),
            Color(0xFF1B1E54),
        )
    } else {
        listOf(
            Color(0xFFF3FAF4),
            Color(0xFFE9F7EC),
            Color(0xFFDEF2E3),
        )
    }

    val bokehColor = if (isDark) {
        Color(0xFF333A8A).copy(alpha = 0.22f)
    } else {
        Color(0xFFA5D6A7).copy(alpha = 0.32f)
    }

    val waveBack = if (isDark) {
        Color(0xFF1E235E).copy(alpha = 0.45f)
    } else {
        Color(0xFF81C784).copy(alpha = 0.35f)
    }

    val waveFront = if (isDark) {
        Color(0xFF16194A).copy(alpha = 0.75f)
    } else {
        Color(0xFF2E7D32).copy(alpha = 0.65f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Yumuşak Bokeh Işık Baloncukları
            drawCircle(
                color = bokehColor,
                radius = w * 0.28f,
                center = Offset(w * 0.12f, h * 0.20f),
            )
            drawCircle(
                color = bokehColor.copy(alpha = bokehColor.alpha * 0.7f),
                radius = w * 0.38f,
                center = Offset(w * 0.88f, h * 0.35f),
            )
            drawCircle(
                color = bokehColor.copy(alpha = bokehColor.alpha * 0.5f),
                radius = w * 0.22f,
                center = Offset(w * 0.25f, h * 0.65f),
            )

            // Alt Dalga Katmanı (Arka)
            val pathBack = Path().apply {
                moveTo(0f, h * 0.94f)
                cubicTo(
                    w * 0.25f, h * 0.90f,
                    w * 0.65f, h * 0.97f,
                    w, h * 0.92f,
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = pathBack, color = waveBack)

            // Alt Dalga Katmanı (Ön)
            val pathFront = Path().apply {
                moveTo(0f, h * 0.96f)
                cubicTo(
                    w * 0.35f, h * 0.99f,
                    w * 0.70f, h * 0.92f,
                    w, h * 0.96f,
                )
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(path = pathFront, color = waveFront)
        }
        content()
    }
}

