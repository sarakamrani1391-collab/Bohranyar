package com.example.data.system

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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

data class UserLocationSnapshot(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val timestampMillis: Long,
    val sourceLabel: String
)

class LocationTracker(private val context: Context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): UserLocationSnapshot? {
        if (!hasLocationPermission()) return null

        val fusedResult = suspendCancellableCoroutine<Location?> { cont ->
            val cancellation = CancellationTokenSource()
            cont.invokeOnCancellation { cancellation.cancel() }
            try {
                fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cancellation.token)
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            cont.resume(location)
                        } else {
                            fusedClient.lastLocation
                                .addOnSuccessListener { last -> cont.resume(last) }
                                .addOnFailureListener { cont.resume(null) }
                        }
                    }
                    .addOnFailureListener {
                        cont.resume(null)
                    }
            } catch (_: Exception) {
                cont.resume(null)
            }
        }

        if (fusedResult != null) {
            return UserLocationSnapshot(
                latitude = fusedResult.latitude,
                longitude = fusedResult.longitude,
                accuracyMeters = fusedResult.accuracy,
                timestampMillis = fusedResult.time.takeIf { it > 0 } ?: System.currentTimeMillis(),
                sourceLabel = "GPS / شبکه"
            )
        }

        // Fallback to Android LocationManager cached provider
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val providers = listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER
            )
            val best = providers.mapNotNull { provider ->
                runCatching { lm.getLastKnownLocation(provider) }.getOrNull()
            }.maxByOrNull { it.time }
            best?.let {
                UserLocationSnapshot(
                    latitude = it.latitude,
                    longitude = it.longitude,
                    accuracyMeters = it.accuracy,
                    timestampMillis = it.time.takeIf { t -> t > 0 } ?: System.currentTimeMillis(),
                    sourceLabel = "آخرین موقعیت ذخیره‌شده دستگاه"
                )
            }
        } catch (_: Exception) {
            null
        }
    }
}

object ImageStorageHelper {
    suspend fun copyImageToInternalStorage(context: Context, uri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                val dir = File(context.filesDir, "report_images").apply { mkdirs() }
                val file = File(dir, "report_${UUID.randomUUID()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                } ?: return@withContext null
                file.absolutePath
            } catch (_: Exception) {
                null
            }
        }
}

object GeoDistanceHelper {
    fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun formatDistanceFa(km: Double): String {
        return if (km < 1.0) {
            "${(km * 1000).toInt()} متر"
        } else {
            String.format(Locale.US, "%.1f کیلومتر", km)
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
