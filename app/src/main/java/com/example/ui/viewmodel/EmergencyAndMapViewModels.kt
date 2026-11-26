package com.example.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ShelterPointEntity
import com.example.data.local.entity.ShelterType
import com.example.data.local.entity.SosRequestEntity
import com.example.data.local.entity.UrbanReportCategory
import com.example.data.local.entity.UrbanReportEntity
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.MapRepository
import com.example.data.repository.ReportRepository
import com.example.data.repository.SosRepository
import com.example.data.system.GeoDistanceHelper
import com.example.data.system.LocationTracker
import com.example.data.system.NetworkMonitor
import com.example.data.system.UserLocationSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SosUiState(
    val selectedEmergencyType: String = "درخواست امداد فوری",
    val customNote: String = "",
    val includeLocation: Boolean = true,
    val isFetchingLocation: Boolean = false,
    val currentLocation: UserLocationSnapshot? = null,
    val locationError: String? = null,
    val isOnline: Boolean = false,
    val userSettings: UserSettings = UserSettings(),
    val history: List<SosRequestEntity> = emptyList(),
    val lastCreatedRequest: SosRequestEntity? = null
) {
    fun buildPreparedSmsText(): String {
        val namePart = userSettings.userName.takeIf { it.isNotBlank() }?.let { "نام: $it\n" } ?: ""
        val bloodPart = userSettings.bloodType.takeIf { it.isNotBlank() && it != "نامشخص" }
            ?.let { "گروه خونی: $it\n" } ?: ""
        val notePart = customNote.trim().takeIf { it.isNotBlank() }?.let { "توضیحات: $it\n" } ?: ""
        val locPart = if (includeLocation && currentLocation != null) {
            "موقعیت: ${String.format(java.util.Locale.US, "%.5f, %.5f", currentLocation.latitude, currentLocation.longitude)}\n" +
                "https://maps.google.com/?q=${currentLocation.latitude},${currentLocation.longitude}"
        } else {
            "موقعیت GPS ثبت نشده است"
        }
        return "درخواست کمک اضطراری - $selectedEmergencyType\n$namePart$bloodPart$notePart$locPart"
    }
}

class SosViewModel(
    private val sosRepository: SosRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val locationTracker: LocationTracker,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val localFormState = MutableStateFlow(SosFormState())

    val uiState: StateFlow<SosUiState> = combine(
        localFormState,
        sosRepository.observeAllRequests(),
        preferencesRepository.userSettings,
        networkMonitor.isOnline
    ) { form, history, settings, online ->
        SosUiState(
            selectedEmergencyType = form.emergencyType,
            customNote = form.customNote,
            includeLocation = form.includeLocation,
            isFetchingLocation = form.isFetchingLocation,
            currentLocation = form.currentLocation,
            locationError = form.locationError,
            isOnline = online,
            userSettings = settings,
            history = history,
            lastCreatedRequest = form.lastCreatedRequest
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SosUiState())

    fun selectEmergencyType(type: String) {
        localFormState.update { it.copy(emergencyType = type) }
    }

    fun updateCustomNote(note: String) {
        localFormState.update { it.copy(customNote = note) }
    }

    fun setIncludeLocation(include: Boolean) {
        localFormState.update { it.copy(includeLocation = include, locationError = null) }
    }

    fun fetchLocation() {
        viewModelScope.launch {
            if (!locationTracker.hasLocationPermission()) {
                localFormState.update {
                    it.copy(locationError = "مجوز موقعیت مکانی هنوز تأیید نشده است.")
                }
                return@launch
            }
            localFormState.update { it.copy(isFetchingLocation = true, locationError = null) }
            val loc = locationTracker.getCurrentLocation()
            if (loc != null) {
                preferencesRepository.setLocationConsent(true)
                localFormState.update {
                    it.copy(
                        isFetchingLocation = false,
                        currentLocation = loc,
                        locationError = null
                    )
                }
            } else {
                localFormState.update {
                    it.copy(
                        isFetchingLocation = false,
                        locationError = "موقعیت مکانی دریافت نشد؛ مطمئن شوید GPS دستگاه روشن است."
                    )
                }
            }
        }
    }

    fun submitSosRequest() {
        viewModelScope.launch {
            val state = uiState.value
            val loc = if (state.includeLocation) state.currentLocation else null
            val created = sosRepository.createSosRequest(
                emergencyType = state.selectedEmergencyType,
                message = state.buildPreparedSmsText(),
                latitude = loc?.latitude,
                longitude = loc?.longitude,
                locationConsentGiven = state.includeLocation && loc != null,
                isOnline = networkMonitor.isOnline.value
            )
            localFormState.update { it.copy(lastCreatedRequest = created) }
        }
    }

    fun markShared(id: Int) {
        viewModelScope.launch {
            sosRepository.markSharedByUser(id)
        }
    }

    fun syncQueuedNow() {
        viewModelScope.launch {
            if (networkMonitor.isOnline.value) {
                sosRepository.syncQueuedRequests()
            }
        }
    }

    fun deleteRequest(id: Int) {
        viewModelScope.launch { sosRepository.deleteRequest(id) }
    }

    fun clearLastCreatedBanner() {
        localFormState.update { it.copy(lastCreatedRequest = null) }
    }

    private data class SosFormState(
        val emergencyType: String = "درخواست امداد فوری",
        val customNote: String = "",
        val includeLocation: Boolean = true,
        val isFetchingLocation: Boolean = false,
        val currentLocation: UserLocationSnapshot? = null,
        val locationError: String? = null,
        val lastCreatedRequest: SosRequestEntity? = null
    )
}

data class ShelterWithDistance(
    val shelter: ShelterPointEntity,
    val distanceKm: Double?
)

data class MapUiState(
    val shelters: List<ShelterWithDistance> = emptyList(),
    val selectedFilter: ShelterType? = null,
    val selectedShelter: ShelterPointEntity? = null,
    val userLocation: UserLocationSnapshot? = null,
    val isFetchingLocation: Boolean = false,
    val locationMessage: String? = null,
    val isOnline: Boolean = false,
    val lastCachedMillis: Long = 0L
)

class MapViewModel(
    private val mapRepository: MapRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val locationTracker: LocationTracker,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val localState = MutableStateFlow(MapLocalState())

    val uiState: StateFlow<MapUiState> = combine(
        mapRepository.observeShelters(),
        localState,
        networkMonitor.isOnline,
        preferencesRepository.userSettings
    ) { shelters, local, online, settings ->
        val filtered = if (local.selectedFilter == null) {
            shelters
        } else {
            shelters.filter { it.type == local.selectedFilter }
        }
        val refLat = local.userLocation?.latitude
        val refLon = local.userLocation?.longitude
        val withDistance = filtered.map { s ->
            val dist = if (refLat != null && refLon != null) {
                GeoDistanceHelper.haversineKm(refLat, refLon, s.latitude, s.longitude)
            } else {
                null
            }
            ShelterWithDistance(s, dist)
        }.sortedBy { it.distanceKm ?: Double.MAX_VALUE }

        val latestCache = shelters.maxOfOrNull { it.cachedAtMillis } ?: settings.lastOfflineSyncMillis
        MapUiState(
            shelters = withDistance,
            selectedFilter = local.selectedFilter,
            selectedShelter = local.selectedShelter,
            userLocation = local.userLocation,
            isFetchingLocation = local.isFetchingLocation,
            locationMessage = local.locationMessage,
            isOnline = online,
            lastCachedMillis = latestCache
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MapUiState())

    fun selectFilter(type: ShelterType?) {
        localState.update { it.copy(selectedFilter = type) }
    }

    fun selectShelter(shelter: ShelterPointEntity?) {
        localState.update { it.copy(selectedShelter = shelter) }
    }

    fun fetchUserLocation() {
        viewModelScope.launch {
            if (!locationTracker.hasLocationPermission()) {
                localState.update { it.copy(locationMessage = "برای نمایش موقعیت روی نقشه، مجوز مکان را تأیید کنید.") }
                return@launch
            }
            localState.update { it.copy(isFetchingLocation = true, locationMessage = null) }
            val loc = locationTracker.getCurrentLocation()
            localState.update {
                if (loc != null) {
                    it.copy(
                        isFetchingLocation = false,
                        userLocation = loc,
                        locationMessage = "موقعیت شما به‌روزرسانی شد (${loc.sourceLabel})"
                    )
                } else {
                    it.copy(
                        isFetchingLocation = false,
                        locationMessage = "دریافت موقعیت ممکن نشد؛ نقشه در حالت آفلاین نمایش داده می‌شود."
                    )
                }
            }
        }
    }

    fun addPersonalSafePoint(name: String, address: String, note: String) {
        viewModelScope.launch {
            val loc = localState.value.userLocation
            val lat = loc?.latitude ?: 35.7000
            val lon = loc?.longitude ?: 51.3900
            mapRepository.addPersonalSafePoint(name, address, lat, lon, note)
        }
    }

    fun deleteShelter(id: Int) {
        viewModelScope.launch {
            mapRepository.deleteShelter(id)
            localState.update { it.copy(selectedShelter = null) }
        }
    }

    fun refreshOfflineSnapshot() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            mapRepository.refreshOfflineCacheTimestamp(now)
            preferencesRepository.updateOfflineSyncTimestamp(now)
            localState.update { it.copy(locationMessage = "نقشه و نقاط امن برای استفاده آفلاین ذخیره شدند.") }
        }
    }

    private data class MapLocalState(
        val selectedFilter: ShelterType? = null,
        val selectedShelter: ShelterPointEntity? = null,
        val userLocation: UserLocationSnapshot? = null,
        val isFetchingLocation: Boolean = false,
        val locationMessage: String? = null
    )
}

data class ReportUiState(
    val reports: List<UrbanReportEntity> = emptyList(),
    val selectedFilter: UrbanReportCategory? = null,
    val isOnline: Boolean = false,
    val currentLocation: UserLocationSnapshot? = null,
    val isFetchingLocation: Boolean = false,
    val statusBanner: String? = null
)

class ReportViewModel(
    private val reportRepository: ReportRepository,
    private val locationTracker: LocationTracker,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val localState = MutableStateFlow(ReportLocalState())

    val uiState: StateFlow<ReportUiState> = combine(
        reportRepository.observeAllReports(),
        localState,
        networkMonitor.isOnline
    ) { reports, local, online ->
        val filtered = if (local.selectedFilter == null) {
            reports
        } else {
            reports.filter { it.category == local.selectedFilter }
        }
        ReportUiState(
            reports = filtered,
            selectedFilter = local.selectedFilter,
            isOnline = online,
            currentLocation = local.currentLocation,
            isFetchingLocation = local.isFetchingLocation,
            statusBanner = local.statusBanner
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReportUiState())

    fun selectFilter(category: UrbanReportCategory?) {
        localState.update { it.copy(selectedFilter = category) }
    }

    fun fetchLocationForReport() {
        viewModelScope.launch {
            if (!locationTracker.hasLocationPermission()) {
                localState.update { it.copy(statusBanner = "مجوز مکان صادر نشده است.") }
                return@launch
            }
            localState.update { it.copy(isFetchingLocation = true) }
            val loc = locationTracker.getCurrentLocation()
            localState.update {
                it.copy(
                    isFetchingLocation = false,
                    currentLocation = loc,
                    statusBanner = if (loc != null) "مختصات مکانی آماده پیوست به گزارش است." else "دریافت موقعیت ناموفق بود."
                )
            }
        }
    }

    fun submitReport(
        category: UrbanReportCategory,
        title: String,
        description: String,
        imageUri: Uri?,
        attachLocation: Boolean
    ) {
        viewModelScope.launch {
            val loc = if (attachLocation) localState.value.currentLocation else null
            val online = networkMonitor.isOnline.value
            reportRepository.submitReport(
                category = category,
                title = title,
                description = description,
                imageUri = imageUri,
                latitude = loc?.latitude,
                longitude = loc?.longitude,
                locationConsentGiven = attachLocation && loc != null,
                isOnline = online
            )
            localState.update {
                it.copy(
                    statusBanner = if (online) {
                        "گزارش ثبت شد و با برچسب «تأییدنشده» در لیست پیگیری قرار گرفت."
                    } else {
                        "به دلیل قطع اینترنت، گزارش در حافظه ذخیره شد و پس از اتصال ارسال می‌شود."
                    }
                )
            }
        }
    }

    fun syncQueuedReports() {
        viewModelScope.launch {
            if (networkMonitor.isOnline.value) {
                reportRepository.syncOfflineReports()
                localState.update { it.copy(statusBanner = "گزارش‌های صف آفلاین همگام‌سازی شدند.") }
            }
        }
    }

    fun deleteReport(id: Int) {
        viewModelScope.launch { reportRepository.deleteReport(id) }
    }

    fun dismissBanner() {
        localState.update { it.copy(statusBanner = null) }
    }

    private data class ReportLocalState(
        val selectedFilter: UrbanReportCategory? = null,
        val currentLocation: UserLocationSnapshot? = null,
        val isFetchingLocation: Boolean = false,
        val statusBanner: String? = null
    )
}
