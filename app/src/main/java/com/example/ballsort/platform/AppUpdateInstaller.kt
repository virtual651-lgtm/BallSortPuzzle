/*
 * com.example.ballsort.platform
 * Uygulama içi doğrudan APK indirme ve kurulum başlatma yardımcısı.
 */
package com.example.ballsort.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

sealed interface UpdateDownloadState {
    data object Idle : UpdateDownloadState
    data class Downloading(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : UpdateDownloadState
    data class ReadyToInstall(val file: File) : UpdateDownloadState
    data class Error(val message: String) : UpdateDownloadState
}

@Singleton
class AppUpdateInstaller @Inject constructor() {

    fun downloadApk(context: Context, downloadUrl: String): Flow<UpdateDownloadState> = flow {
        emit(UpdateDownloadState.Downloading(progress = 0f, downloadedBytes = 0L, totalBytes = -1L))
        var connection: HttpURLConnection? = null
        try {
            val updatesDir = File(context.cacheDir, "updates").apply { if (!exists()) mkdirs() }
            val outputFile = File(updatesDir, "update_latest.apk")
            if (outputFile.exists()) outputFile.delete()

            var targetUrl = downloadUrl
            var redirects = 0
            while (redirects < 5) {
                val url = URL(targetUrl)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 10_000
                    readTimeout = 15_000
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", "RenkTupleri-App")
                }
                val code = connection.responseCode
                if (code in 300..399) {
                    val location = connection.getHeaderField("Location") ?: break
                    targetUrl = location
                    connection.disconnect()
                    redirects++
                } else {
                    break
                }
            }

            val conn = connection ?: throw IllegalStateException("Bağlantı kurulamadı")
            if (conn.responseCode != HttpURLConnection.HTTP_OK) {
                emit(UpdateDownloadState.Error("HTTP ${conn.responseCode}"))
                return@flow
            }

            val totalBytes = conn.contentLengthLong
            var downloaded = 0L

            conn.inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var read: Int
                    var lastEmittedProgress = 0f

                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        downloaded += read
                        val progress = if (totalBytes > 0) (downloaded.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f
                        if (progress - lastEmittedProgress >= 0.02f || downloaded == totalBytes) {
                            lastEmittedProgress = progress
                            emit(UpdateDownloadState.Downloading(progress, downloaded, totalBytes))
                        }
                    }
                    output.flush()
                }
            }

            emit(UpdateDownloadState.ReadyToInstall(outputFile))
        } catch (e: Exception) {
            emit(UpdateDownloadState.Error(e.message ?: "İndirme başarısız"))
        } finally {
            connection?.disconnect()
        }
    }.flowOn(Dispatchers.IO)

    fun installApk(context: Context, apkFile: File) {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
        } else {
            Uri.fromFile(apkFile)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }
}

