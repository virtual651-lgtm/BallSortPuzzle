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

@Composable
fun GameBackdrop(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val gradientColors = if (isDark) {
        listOf(
            Color(0xFF0B1022),
            Color(0xFF151E3A),
            Color(0xFF1C1736),
        )
    } else {
        listOf(
            Color(0xFFEAF2FF),
            Color(0xFFF5F3FF),
            Color(0xFFFFF2F8),
        )
    }

    val glowA = if (isDark) Color(0xFF4F8DFF).copy(alpha = 0.14f) else Color(0xFF5C8EFF).copy(alpha = 0.12f)
    val glowB = if (isDark) Color(0xFFB36BFF).copy(alpha = 0.12f) else Color(0xFFC77DFF).copy(alpha = 0.10f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowA, Color.Transparent),
                    center = Offset(size.width * 0.18f, size.height * 0.16f),
                    radius = size.maxDimension * 0.45f,
                ),
                radius = size.maxDimension * 0.45f,
                center = Offset(size.width * 0.18f, size.height * 0.16f),
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowB, Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.78f),
                    radius = size.maxDimension * 0.50f,
                ),
                radius = size.maxDimension * 0.50f,
                center = Offset(size.width * 0.85f, size.height * 0.78f),
            )
        }
        content()
    }
}

