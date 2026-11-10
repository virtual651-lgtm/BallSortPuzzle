/*
 * com.example.ballsort.platform
 * API 24+ uyumlu titreşim geri bildirim sarmalayıcısı.
 */
package com.example.ballsort.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Haptics @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun click(enabled: Boolean) = vibrate(enabled, 18L)
    fun error(enabled: Boolean) = vibrate(enabled, 45L)
    fun tubeComplete(enabled: Boolean) = vibrate(enabled, 65L)

    fun win(enabled: Boolean) {
        if (!enabled) return
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 55, 60, 90), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(longArrayOf(0, 55, 60, 90), -1)
            }
        }
    }

    private fun vibrate(enabled: Boolean, durationMs: Long) {
        if (!enabled) return
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vib.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(durationMs)
            }
        }
    }
}

