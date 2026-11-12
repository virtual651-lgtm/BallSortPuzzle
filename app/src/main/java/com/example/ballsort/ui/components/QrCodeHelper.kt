/*
 * com.example.ballsort.ui.components
 * Wi-Fi düellosu için QR kod üretimi ve çözümleme yardımcısı.
 */
package com.example.ballsort.ui.components

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QrCodeHelper {

    fun encodeEndpoint(ip: String, port: Int): String = "renktupleri://$ip:$port"

    fun parseEndpoint(raw: String): Pair<String, Int>? {
        val cleaned = raw.trim().removePrefix("renktupleri://")
        val parts = cleaned.split(":")
        if (parts.size != 2) return null
        val host = parts[0].trim()
        val port = parts[1].trim().toIntOrNull() ?: return null
        if (host.isEmpty() || port <= 0) return null
        return host to port
    }

    fun generateQrBitmap(content: String, sizePx: Int = 512): ImageBitmap? = runCatching {
        val matrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        for (x in 0 until sizePx) {
            for (y in 0 until sizePx) {
                bmp.setPixel(x, y, if (matrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
            }
        }
        bmp.asImageBitmap()
    }.getOrNull()
}

