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

    // Ses seviyesi 65 olarak ayarlandı — daha net ve tatmin edici
    private var toneGenerator: ToneGenerator? = runCatching {
        ToneGenerator(AudioManager.STREAM_MUSIC, 65)
    }.getOrNull()

    fun playSelect(enabled: Boolean) {
        if (!enabled) return
        // Kısa, net ve hafif seçim tonu
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35) }
    }

    fun playPlace(enabled: Boolean) {
        if (!enabled) return
        // Top yerleştirildiğinde tok ve tatmin edici dokunuş tonu
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 50) }
    }

    fun playInvalid(enabled: Boolean) {
        if (!enabled) return
        // Geçersiz hamle geri bildirimi
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 80) }
    }

    fun playTubeCompleted(enabled: Boolean) {
        if (!enabled) return
        // Tüp tamamlandığında neşeli çınlama tonu
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 160) }
    }

    fun playWin(enabled: Boolean) {
        if (!enabled) return
        // Seviye kazanıldığında zafer teyit tonu
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_CDMA_CONFIRM, 260) }
    }

    fun playChest(enabled: Boolean) {
        if (!enabled) return
        // Hazine sandığı açıldığında zengin kutlama tonu
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_SUP_CONFIRM, 320) }
    }

    fun playSwap(enabled: Boolean) {
        if (!enabled) return
        // Takas gücü kullanıldığında sihir tonu
        runCatching { toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 90) }
    }

    fun release() {
        runCatching { toneGenerator?.release() }
        toneGenerator = null
    }
}

