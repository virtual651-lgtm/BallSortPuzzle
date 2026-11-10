/*
 * com.example.ballsort.platform
 * Kısa oyun ses efektleri (res/raw dosyası gerektirmeden ToneGenerator yedekli).
 */
package com.example.ballsort.platform

import android.media.AudioManager
import android.media.ToneGenerator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundPlayer @Inject constructor() {

    // VARSAYIM: res/raw altında özel .ogg ses dosyaları henüz olmadığı için
    // sistem ToneGenerator ile hafif etkileşim tonları üretilir.
    private var toneGenerator: ToneGenerator? = runCatching {
        ToneGenerator(AudioManager.STREAM_MUSIC, 45)
    }.getOrNull()

    fun playSelect(enabled: Boolean) {
        if (!enabled) return
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 45) }
    }

    fun playPlace(enabled: Boolean) {
        if (!enabled) return
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 65) }
    }

    fun playInvalid(enabled: Boolean) {
        if (!enabled) return
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 90) }
    }

    fun playTubeCompleted(enabled: Boolean) {
        if (!enabled) return
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 130) }
    }

    fun playWin(enabled: Boolean) {
        if (!enabled) return
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_SUP_CONFIRM, 240) }
    }

    fun release() {
        runCatching { toneGenerator?.release() }
        toneGenerator = null
    }
}

