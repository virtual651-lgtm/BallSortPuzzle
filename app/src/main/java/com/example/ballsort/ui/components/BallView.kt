/*
 * com.example.ballsort.ui.components
 * Radyal gradyanlı, parlama efektli ve renk körlüğü sembolü destekli top çizimi.
 */
package com.example.ballsort.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.example.ballsort.ui.theme.BallSkin
import com.example.ballsort.ui.theme.BallSymbol
import com.example.ballsort.ui.theme.ballEntryFor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BallView(
    colorId: Int,
    ballSize: Dp,
    colorBlind: Boolean,
    modifier: Modifier = Modifier,
    ballSkin: String = BallSkin.CLASSIC.id,
) {
    val entry = ballEntryFor(colorId)
    Canvas(modifier = modifier.size(ballSize)) {
        drawBall(
            color = entry.color,
            symbol = entry.symbol,
            colorBlind = colorBlind,
            center = Offset(size.width / 2f, size.height / 2f),
            radius = size.minDimension / 2f,
            ballSkin = ballSkin,
        )
    }
}

fun DrawScope.drawBall(
    color: Color,
    symbol: BallSymbol,
    colorBlind: Boolean,
    center: Offset,
    radius: Float,
    ballSkin: String = BallSkin.CLASSIC.id,
) {
    when (BallSkin.fromId(ballSkin)) {
        BallSkin.CLASSIC -> {
            val lighter = Color(
                red = (color.red + (1f - color.red) * 0.45f).coerceIn(0f, 1f),
                green = (color.green + (1f - color.green) * 0.45f).coerceIn(0f, 1f),
                blue = (color.blue + (1f - color.blue) * 0.45f).coerceIn(0f, 1f),
                alpha = 1f,
            )
            val darker = Color(
                red = (color.red * 0.65f).coerceIn(0f, 1f),
                green = (color.green * 0.65f).coerceIn(0f, 1f),
                blue = (color.blue * 0.65f).coerceIn(0f, 1f),
                alpha = 1f,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(lighter, color, darker),
                    center = Offset(center.x - radius * 0.28f, center.y - radius * 0.28f),
                    radius = radius * 1.35f,
                ),
                radius = radius,
                center = center,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.42f),
                radius = radius * 0.22f,
                center = Offset(center.x - radius * 0.34f, center.y - radius * 0.34f),
            )
        }
        BallSkin.NEON -> {
            drawCircle(
                color = Color(0xFF10141E),
                radius = radius,
                center = center,
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.35f), Color.Transparent),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
            drawCircle(
                color = color,
                radius = radius * 0.84f,
                center = center,
                style = Stroke(width = (radius * 0.24f).coerceAtLeast(3f)),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.7f),
                radius = radius * 0.16f,
                center = Offset(center.x - radius * 0.28f, center.y - radius * 0.28f),
            )
        }
        BallSkin.GEM -> {
            val darker = Color(
                red = (color.red * 0.55f).coerceIn(0f, 1f),
                green = (color.green * 0.55f).coerceIn(0f, 1f),
                blue = (color.blue * 0.55f).coerceIn(0f, 1f),
                alpha = 1f,
            )
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(color, darker),
                    start = Offset(center.x - radius, center.y - radius),
                    end = Offset(center.x + radius, center.y + radius),
                ),
                radius = radius,
                center = center,
            )
            val facet = Path().apply {
                moveTo(center.x, center.y - radius * 0.68f)
                lineTo(center.x + radius * 0.62f, center.y)
                lineTo(center.x, center.y + radius * 0.68f)
                lineTo(center.x - radius * 0.62f, center.y)
                close()
            }
            drawPath(facet, color = Color.White.copy(alpha = 0.28f))
            drawPath(facet, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = 1.5f))
        }
        BallSkin.MINIMAL -> {
            drawCircle(
                color = color,
                radius = radius,
                center = center,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = radius * 0.88f,
                center = center,
                style = Stroke(width = (radius * 0.12f).coerceAtLeast(1.5f)),
            )
        }
    }

    if (colorBlind) {
        drawSymbol(
            symbol = symbol,
            center = center,
            r = radius * 0.44f,
            tint = Color.White.copy(alpha = 0.92f),
        )
    }
}

private fun DrawScope.drawSymbol(
    symbol: BallSymbol,
    center: Offset,
    r: Float,
    tint: Color,
) {
    val stroke = Stroke(width = (r * 0.34f).coerceAtLeast(2f), cap = StrokeCap.Round)
    when (symbol) {
        BallSymbol.CIRCLE -> drawCircle(color = tint, radius = r * 0.7f, center = center)
        BallSymbol.RING -> drawCircle(color = tint, radius = r * 0.72f, center = center, style = stroke)
        BallSymbol.DOT -> drawCircle(color = tint, radius = r * 0.35f, center = center)
        BallSymbol.SQUARE -> drawRect(
            color = tint,
            topLeft = Offset(center.x - r * 0.65f, center.y - r * 0.65f),
            size = Size(r * 1.3f, r * 1.3f),
            style = stroke,
        )
        BallSymbol.TRIANGLE -> {
            val path = Path().apply {
                moveTo(center.x, center.y - r * 0.8f)
                lineTo(center.x + r * 0.75f, center.y + r * 0.6f)
                lineTo(center.x - r * 0.75f, center.y + r * 0.6f)
                close()
            }
            drawPath(path, color = tint, style = stroke)
        }
        BallSymbol.DIAMOND -> {
            val path = Path().apply {
                moveTo(center.x, center.y - r * 0.85f)
                lineTo(center.x + r * 0.75f, center.y)
                lineTo(center.x, center.y + r * 0.85f)
                lineTo(center.x - r * 0.75f, center.y)
                close()
            }
            drawPath(path, color = tint, style = stroke)
        }
        BallSymbol.CROSS -> {
            drawLine(
                color = tint,
                start = Offset(center.x - r * 0.6f, center.y - r * 0.6f),
                end = Offset(center.x + r * 0.6f, center.y + r * 0.6f),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = tint,
                start = Offset(center.x + r * 0.6f, center.y - r * 0.6f),
                end = Offset(center.x - r * 0.6f, center.y + r * 0.6f),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round,
            )
        }
        BallSymbol.PLUS -> {
            drawLine(
                color = tint,
                start = Offset(center.x, center.y - r * 0.7f),
                end = Offset(center.x, center.y + r * 0.7f),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round,
            )
            drawLine(
                color = tint,
                start = Offset(center.x - r * 0.7f, center.y),
                end = Offset(center.x + r * 0.7f, center.y),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round,
            )
        }
        BallSymbol.STAR -> {
            val path = Path()
            for (i in 0 until 10) {
                val angle = Math.toRadians((-90 + i * 36).toDouble())
                val rad = if (i % 2 == 0) r * 0.85f else r * 0.38f
                val x = center.x + (rad * cos(angle)).toFloat()
                val y = center.y + (rad * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, color = tint)
        }
        BallSymbol.HEX -> {
            val path = Path()
            for (i in 0 until 6) {
                val angle = Math.toRadians((i * 60).toDouble())
                val x = center.x + (r * 0.78f * cos(angle)).toFloat()
                val y = center.y + (r * 0.78f * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, color = tint, style = stroke)
        }
        BallSymbol.WAVE -> {
            val path = Path().apply {
                moveTo(center.x - r * 0.75f, center.y)
                quadraticTo(center.x - r * 0.35f, center.y - r * 0.65f, center.x, center.y)
                quadraticTo(center.x + r * 0.35f, center.y + r * 0.65f, center.x + r * 0.75f, center.y)
            }
            drawPath(path, color = tint, style = stroke)
        }
        BallSymbol.SLASH -> {
            drawLine(
                color = tint,
                start = Offset(center.x - r * 0.6f, center.y + r * 0.6f),
                end = Offset(center.x + r * 0.6f, center.y - r * 0.6f),
                strokeWidth = stroke.width,
                cap = StrokeCap.Round,
            )
        }
    }
}

