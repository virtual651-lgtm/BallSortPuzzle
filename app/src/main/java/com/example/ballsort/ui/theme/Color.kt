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

val SeedPrimaryLight = Color(0xFF2E7D32)      // Orman Yeşili (Açık tema Başla butonu)
val SeedSecondaryLight = Color(0xFF388E3C)    // Nane Yeşili ikincil
val SeedTertiaryLight = Color(0xFF1B5E20)     // Zümrüt Koyu Yeşili

val SeedPrimaryDark = Color(0xFFFFB300)       // Canlı Altın Sarısı (Koyu tema Başla butonu)
val SeedSecondaryDark = Color(0xFF4F8DFF)     // Kraliyet Mavisi
val SeedTertiaryDark = Color(0xFFFFA000)      // Kehribar Sarısı

// Royal Kingdom & Renk Tüpleri Özel Renkleri
val RoyalDarkBg = Color(0xFF0C1033)
val RoyalDarkSurface = Color(0xFF15193B)
val RoyalDarkCard = Color(0xFF1A1F4D)
val RoyalDarkButton = Color(0xFFFFB300)

val RoyalLightBg = Color(0xFFF3FAF4)
val RoyalLightSurface = Color(0xFFFFFFFF)
val RoyalLightCard = Color(0xFFE8F5E9)
val RoyalLightButton = Color(0xFF2E7D32)
val RoyalLightEmeraldText = Color(0xFF1B4332)

