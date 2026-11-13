/*
 * com.example.ballsort.ui.splash
 * Tam ekran dikey görsel ve dolan casual yükleme çubuğu içeren açılış ekranı.
 */
package com.example.ballsort.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballsort.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
) {
    // 0.0f -> 1.0f arası yumuşak dolma animasyonu
    val progressAnim = remember { Animatable(0f) }

    // Çubuk üzerinde hafifçe parıldayan ışıma efekti
    val infiniteTransition = rememberInfiniteTransition(label = "SplashShimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerX",
    )

    LaunchedEffect(Unit) {
        // İlerleme çubuğunun gerçekçi dolması: 1.6 saniye
        progressAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1600,
                easing = FastOutSlowInEasing,
            ),
        )
        // 100% olduktan sonra kısa bir bekleyiş ve ana menüye geçiş
        delay(120L)
        onSplashFinished()
    }

    val currentProgress = progressAnim.value
    val percentText = (currentProgress * 100).toInt().coerceIn(0, 100)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFEBDC)),
    ) {
        // Tam ekran 3D görsel (arka plan)
        Image(
            painter = painterResource(id = R.drawable.splash_fullscreen),
            contentDescription = "Renk Tüpleri Açılış Ekranı",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        // Alt kısımdaki "Yükleniyor... %XX" ve dolan altın çerçeveli bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 36.dp, start = 32.dp, end = 32.dp),
        ) {
            // "Yükleniyor... %XX" metni
            Text(
                text = "Yükleniyor... %$percentText",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(
                        color = Color(0xFF1F0D1B).copy(alpha = 0.95f),
                        offset = Offset(2f, 3f),
                        blurRadius = 6f,
                    ),
                ),
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Dolan altın çerçeveli çubuk
            val barShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(20.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = barShape,
                        spotColor = Color(0xFF26101B),
                    )
                    .clip(barShape)
                    .background(Color(0xFF2E1724))
                    .border(
                        BorderStroke(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFFFFE082),
                                    Color(0xFFFFB300),
                                    Color(0xFFFFA000),
                                    Color(0xFFFFECB3),
                                ),
                            ),
                        ),
                        shape = barShape,
                    )
                    .padding(3.dp), // İç çubuğun kenarlıkla mesafesi
            ) {
                // Dolan kısım
                if (currentProgress > 0.02f) {
                    val innerShape = RoundedCornerShape(8.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(currentProgress)
                            .fillMaxHeight()
                            .clip(innerShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFFCA28),
                                        Color(0xFFFFA726),
                                        Color(0xFFFFB300),
                                    ),
                                ),
                            ),
                    ) {
                        // Parlaklık / Işıma şeridi (üst yarı saydamlık)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.45f)
                                .background(
                                    Color.White.copy(alpha = 0.35f),
                                ),
                        )

                        // Canlı Shimmer sweep parıltısı
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.40f),
                                            Color.Transparent,
                                        ),
                                        start = Offset(shimmerOffset, 0f),
                                        end = Offset(shimmerOffset + 80f, 20f),
                                    ),
                                ),
                        )
                    }
                }
            }
        }
    }
}

