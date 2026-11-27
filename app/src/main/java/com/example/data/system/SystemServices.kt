package com.example.data.system

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import com.example.data.local.entity.MediaCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.sin

class NetworkMonitor(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isOnline = MutableStateFlow(checkCurrentConnectivity())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isOnline.value = checkCurrentConnectivity()
        }

        override fun onLost(network: Network) {
            _isOnline.value = checkCurrentConnectivity()
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            _isOnline.value = checkCurrentConnectivity()
        }
    }

    init {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, callback)
        } catch (_: Exception) {
            _isOnline.value = checkCurrentConnectivity()
        }
    }

    private fun checkCurrentConnectivity(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}

object MediaFileStorageHelper {
    private fun directoryFor(context: Context, category: MediaCategory): File {
        val sub = when (category) {
            MediaCategory.FIRST_AID -> "media/first_aid"
            MediaCategory.MAVA_CARTOON -> "media/mava_cartoons"
        }
        return File(context.filesDir, sub).apply { mkdirs() }
    }

    suspend fun importLocalVideo(
        context: Context,
        mediaId: String,
        category: MediaCategory,
        sourceUri: Uri
    ): String? = withContext(Dispatchers.IO) {
        try {
            val outFile = File(directoryFor(context, category), "${mediaId}.mp4")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            } ?: return@withContext null
            outFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    suspend fun downloadRemoteVideo(
        context: Context,
        mediaId: String,
        category: MediaCategory,
        streamUrl: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            val conn = (URL(streamUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 30_000
            }
            if (conn.responseCode !in 200..299) {
                conn.disconnect()
                return@withContext null
            }
            val outFile = File(directoryFor(context, category), "${mediaId}.mp4")
            conn.inputStream.use { input ->
                outFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            conn.disconnect()
            outFile.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    suspend fun deleteLocalVideo(localPath: String?): Boolean = withContext(Dispatchers.IO) {
        if (localPath.isNullOrBlank()) return@withContext false
        runCatching { File(localPath).delete() }.getOrDefault(false)
    }
}

/**
 * Generates soft, soothing sine-wave chimes for Ma'va toddlers without sudden/loud sounds
 * or external copyrighted audio files.
 */
object MavaSoundEngine {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun playGentleChime(freqHz: Double = 523.25, durationMs: Int = 260, enabled: Boolean = true) {
        if (!enabled) return
        scope.launch {
            playTone(freqHz, durationMs, amplitude = 0.14)
        }
    }

    fun playHappyMelody(enabled: Boolean = true) {
        if (!enabled) return
        scope.launch {
            playTone(523.25, 160, 0.12)
            playTone(659.25, 160, 0.12)
            playTone(783.99, 240, 0.12)
        }
    }

    fun playCalmLullabyNote(step: Int, enabled: Boolean = true) {
        if (!enabled) return
        val pentatonic = listOf(261.63, 293.66, 329.63, 392.00, 440.00, 392.00, 329.63)
        val freq = pentatonic[step % pentatonic.size]
        scope.launch {
            playTone(freq, 420, amplitude = 0.09)
        }
    }

    private fun playTone(freqHz: Double, durationMs: Int, amplitude: Double) {
        try {
            val sampleRate = 22050
            val count = (sampleRate * durationMs) / 1000
            val samples = ShortArray(count)
            for (i in 0 until count) {
                val env = when {
                    i < count * 0.15 -> i / (count * 0.15)
                    i > count * 0.70 -> (count - i) / (count * 0.30)
                    else -> 1.0
                }.coerceIn(0.0, 1.0)
                val angle = 2.0 * Math.PI * i * freqHz / sampleRate
                samples[i] = (sin(angle) * env * amplitude * Short.MAX_VALUE).toInt().toShort()
            }
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(samples, 0, samples.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 40L)
            track.release()
        } catch (_: Exception) {
            // Audio device unavailable in headless tests
        }
    }
}

object PersianTimeFormatter {
    fun formatTimestamp(millis: Long): String {
        if (millis <= 0L) return "ثبت نشده"
        val diff = System.currentTimeMillis() - millis
        return when {
            diff < 60_000L -> "هم‌اکنون"
            diff < 3_600_000L -> "${diff / 60_000L} دقیقه پیش"
            diff < 86_400_000L -> "${diff / 3_600_000L} ساعت پیش"
            else -> {
                val sdf = SimpleDateFormat("yyyy/MM/dd - HH:mm", Locale.US)
                sdf.format(Date(millis))
            }
        }
    }
}
