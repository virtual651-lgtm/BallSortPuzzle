/*
 * com.example.ballsort.data
 * GitHub Releases / statik version.json üzerinden ücretsiz sürüm kontrolü yapar.
 */
package com.example.ballsort.data

import com.example.ballsort.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateChecker @Inject constructor(
    private val json: Json,
) {
    /**
     * Uzak version.json dosyasını kontrol eder; daha yeni sürüm varsa [AppUpdateInfo] döner.
     * İnternet yoksa veya örnek URL değiştirilmemişse sessizce null döner.
     */
    suspend fun checkForUpdate(): AppUpdateInfo? = withContext(Dispatchers.IO) {
        val urlString = BuildConfig.UPDATE_CHECK_URL
        if (urlString.contains("KULLANICI_ADIN")) return@withContext null

        var connection: HttpURLConnection? = null
        try {
            connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
                connectTimeout = 4_000
                readTimeout = 4_000
                requestMethod = "GET"
            }
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return@withContext null
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val info = json.decodeFromString<AppUpdateInfo>(body)
            if (info.latestVersionCode > BuildConfig.VERSION_CODE) info else null
        } catch (_: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }
}

