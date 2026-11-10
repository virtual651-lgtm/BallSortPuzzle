/*
 * com.example.ballsort.ui.theme
 * 12 yüksek kontrastlı top rengi, renk körlüğü sembolleri ve tema renk şeması.
 */
package com.example.ballsort.ui.theme

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.ballsort.R

enum class BallSymbol {
    CIRCLE,
    TRIANGLE,
    SQUARE,
    DIAMOND,
    STAR,
    CROSS,
    RING,
    PLUS,
    HEX,
    WAVE,
    SLASH,
    DOT,
}

data class BallPaletteEntry(
    val color: Color,
    val symbol: BallSymbol,
    @StringRes val nameRes: Int,
)

// VARSAYIM: Yüksek kontrastlı 12 renkli oyun paleti (açık/koyu tema bağımsız sabit kalır).
val BallPalette = listOf(
    BallPaletteEntry(Color(0xFFE53935), BallSymbol.CIRCLE, R.string.color_red),
    BallPaletteEntry(Color(0xFF1E88E5), BallSymbol.TRIANGLE, R.string.color_blue),
    BallPaletteEntry(Color(0xFFFDD835), BallSymbol.SQUARE, R.string.color_yellow),
    BallPaletteEntry(Color(0xFF43A047), BallSymbol.DIAMOND, R.string.color_green),
    BallPaletteEntry(Color(0xFF8E24AA), BallSymbol.STAR, R.string.color_purple),
    BallPaletteEntry(Color(0xFFFB8C00), BallSymbol.CROSS, R.string.color_orange),
    BallPaletteEntry(Color(0xFF00ACC1), BallSymbol.RING, R.string.color_cyan),
    BallPaletteEntry(Color(0xFFEC407A), BallSymbol.PLUS, R.string.color_pink),
    BallPaletteEntry(Color(0xFFC0CA33), BallSymbol.HEX, R.string.color_lime),
    BallPaletteEntry(Color(0xFF795548), BallSymbol.WAVE, R.string.color_brown),
    BallPaletteEntry(Color(0xFF3949AB), BallSymbol.SLASH, R.string.color_indigo),
    BallPaletteEntry(Color(0xFF00897B), BallSymbol.DOT, R.string.color_teal),
)

fun ballEntryFor(colorId: Int): BallPaletteEntry =
    BallPalette[colorId.mod(BallPalette.size)]

val SeedPrimaryLight = Color(0xFF3B5BA9)
val SeedSecondaryLight = Color(0xFF575E71)
val SeedTertiaryLight = Color(0xFF725572)

val SeedPrimaryDark = Color(0xFFB3C5FF)
val SeedSecondaryDark = Color(0xFFBFC6DC)
val SeedTertiaryDark = Color(0xFFDFBBDE)

