package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.SosRequestEntity
import com.example.data.local.entity.SosStatus
import com.example.data.model.CrisisType
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.FamilyRepository
import com.example.data.repository.SosRepository
import com.example.data.system.EmergencyLocationSnapshot
import com.example.data.system.NetworkMonitor
import com.example.data.system.SosLocationStatus
import com.example.data.system.SosLocationTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

data class EmergencyRecipient(
    val id: String,
    val name: String,
    val relation: String,
    val phone: String
)

data class SosUiState(
    val selectedCrisisType: CrisisType = CrisisType.EARTHQUAKE,
    val selectedEmergencyType: String = "درخواست امداد فوری",
    val addressNote: String = "",
    val customNote: String = "",
    val isOnline: Boolean = false,
    val userSettings: UserSettings = UserSettings(),
    val availableContacts: List<EmergencyRecipient> = emptyList(),
    val selectedContactIds: Set<String> = emptySet(),
    val locationConsentGiven: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val isLocationSharingActive: Boolean = false,
    val locationStatus: SosLocationStatus = SosLocationStatus.IDLE,
    val liveLocation: EmergencyLocationSnapshot? = null,
    val activeSosRequestId: Int? = null,
    val history: List<SosRequestEntity> = emptyList(),
    val lastCreatedRequest: SosRequestEntity? = null
) {
    fun buildSelectedContactPhones(): String =
        availableContacts
            .filter { it.id in selectedContactIds && it.phone.isNotBlank() }
            .joinToString(",") { it.phone.trim() }

    fun buildPreparedSmsText(): String {
        val namePart = userSettings.userName.takeIf { it.isNotBlank() }?.let { "نام: $it\n" } ?: ""
        val cityPart = userSettings.userCity.takeIf { it.isNotBlank() }?.let { "شهر: $it\n" } ?: ""
        val bloodPart = userSettings.bloodType.takeIf { it.isNotBlank() && it != "نامشخص" }
            ?.let { "گروه خونی: $it\n" } ?: ""
        val coordsPart = if (locationConsentGiven && liveLocation != null) {
            val latStr = String.format(Locale.US, "%.5f", liveLocation.latitude)
            val lonStr = String.format(Locale.US, "%.5f", liveLocation.longitude)
            "موقعیت زنده GPS: $latStr, $lonStr\nنقشه: https://maps.google.com/?q=$latStr,$lonStr\n"
        } else {
            ""
        }
        val addressPart = addressNote.trim().takeIf { it.isNotBlank() }?.let { "نشانی تقریبی: $it\n" } ?: ""
        val notePart = customNote.trim().takeIf { it.isNotBlank() }?.let { "توضیحات: $it\n" } ?: ""
        return "درخواست کمک اضطراری - $selectedEmergencyType (بحران: ${selectedCrisisType.titleFa})\n" +
            "$namePart$cityPart$bloodPart$coordsPart$addressPart$notePart"
    }
}

class SosViewModel(
    private val sosRepository: SosRepository,
    private val familyRepository: FamilyRepository,
    preferencesRepository: UserPreferencesRepository,
    private val networkMonitor: NetworkMonitor,
    private val locationTracker: SosLocationTracker
) : ViewModel() {

    private val localFormState = MutableStateFlow(
        SosFormState(hasPermission = locationTracker.hasLocationPermission())
    )
    private var periodicLocationJob: Job? = null

    private val contactsFlow = combine(
        familyRepository.observeMembers(),
        familyRepository.observePlan()
    ) { members, plan ->
        buildList {
            members.filter { it.phone.isNotBlank() }.forEach { m ->
                add(
                    EmergencyRecipient(
                        id = "member_${m.id}",
                        name = m.name,
                        relation = m.relation.ifBlank { "عضو خانواده" },
                        phone = m.phone.trim()
                    )
                )
            }
            if (plan != null && plan.outOfCityContactPhone.isNotBlank()) {
                add(
                    EmergencyRecipient(
                        id = "out_of_city",
                        name = plan.outOfCityContactName.ifBlank { "تماس خارج از شهر" },
                        relation = "مخاطب اضطراری خارج از شهر",
                        phone = plan.outOfCityContactPhone.trim()
                    )
                )
            }
        }
    }

    private val locationFlow = combine(
        locationTracker.currentLocation,
        locationTracker.status,
        locationTracker.isTracking
    ) { snapshot, status, isTracking ->
        Triple(snapshot, status, isTracking)
    }

    val uiState: StateFlow<SosUiState> = combine(
        localFormState,
        combine(sosRepository.observeAllRequests(), preferencesRepository.userSettings, networkMonitor.isOnline) { history, settings, online ->
            Triple(history, settings, online)
        },
        contactsFlow,
        locationFlow
    ) { form, repoTriple, contacts, locTriple ->
        val (history, settings, online) = repoTriple
        val (snapshot, locStatus, tracking) = locTriple
        val selectedIds = contacts
            .map { it.id }
            .filter { it !in form.deselectedContactIds }
            .toSet()

        SosUiState(
            selectedCrisisType = form.crisisType,
            selectedEmergencyType = form.emergencyType,
            addressNote = form.addressNote,
            customNote = form.customNote,
            isOnline = online,
            userSettings = settings,
            availableContacts = contacts,
            selectedContactIds = selectedIds,
            locationConsentGiven = form.locationConsentGiven,
            hasLocationPermission = form.hasPermission,
            isLocationSharingActive = form.activeSosRequestId != null && form.locationConsentGiven && tracking,
            locationStatus = locStatus,
            liveLocation = snapshot,
            activeSosRequestId = form.activeSosRequestId,
            history = history,
            lastCreatedRequest = form.lastCreatedRequest
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SosUiState(hasLocationPermission = locationTracker.hasLocationPermission())
    )

    fun selectCrisisType(crisisType: CrisisType) {
        localFormState.update { it.copy(crisisType = crisisType) }
    }

    fun selectEmergencyType(type: String) {
        localFormState.update { it.copy(emergencyType = type) }
    }

    fun updateAddressNote(address: String) {
        localFormState.update { it.copy(addressNote = address) }
    }

    fun updateCustomNote(note: String) {
        localFormState.update { it.copy(customNote = note) }
    }

    fun toggleContactSelection(contactId: String) {
        localFormState.update { form ->
            val updated = if (contactId in form.deselectedContactIds) {
                form.deselectedContactIds - contactId
            } else {
                form.deselectedContactIds + contactId
            }
            form.copy(deselectedContactIds = updated)
        }
    }

    fun setLocationConsent(consent: Boolean) {
        localFormState.update {
            it.copy(
                locationConsentGiven = consent,
                hasPermission = locationTracker.hasLocationPermission()
            )
        }
        if (!consent) {
            stopLiveLocationTrackingOnly(SosLocationStatus.IDLE)
        } else {
            val activeId = localFormState.value.activeSosRequestId
            if (activeId != null) {
                if (locationTracker.hasLocationPermission()) {
                    startPeriodicLocationUpdates(activeId)
                } else {
                    locationTracker.markPermissionDenied()
                }
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        localFormState.update {
            it.copy(
                hasPermission = granted,
                locationConsentGiven = if (granted) true else it.locationConsentGiven
            )
        }
        if (granted) {
            val activeId = localFormState.value.activeSosRequestId
            if (activeId != null && localFormState.value.locationConsentGiven) {
                startPeriodicLocationUpdates(activeId)
            }
        } else {
            locationTracker.markPermissionDenied()
        }
    }

    fun submitSosRequest(shareLiveLocation: Boolean = localFormState.value.locationConsentGiven) {
        viewModelScope.launch {
            val permissionNow = locationTracker.hasLocationPermission()
            localFormState.update {
                it.copy(
                    locationConsentGiven = shareLiveLocation,
                    hasPermission = permissionNow
                )
            }
            val currentState = uiState.value.copy(
                locationConsentGiven = shareLiveLocation,
                hasLocationPermission = permissionNow
            )
            val initialLoc = if (shareLiveLocation && permissionNow) {
                locationTracker.currentLocation.value
            } else {
                null
            }
            val created = sosRepository.createSosRequest(
                emergencyType = "${currentState.selectedCrisisType.titleFa} - ${currentState.selectedEmergencyType}",
                message = currentState.buildPreparedSmsText(),
                isOnline = networkMonitor.isOnline.value,
                latitude = initialLoc?.latitude,
                longitude = initialLoc?.longitude,
                locationConsentGiven = shareLiveLocation && permissionNow
            )
            localFormState.update {
                it.copy(
                    lastCreatedRequest = created,
                    activeSosRequestId = created.id
                )
            }

            if (shareLiveLocation) {
                if (permissionNow) {
                    startPeriodicLocationUpdates(created.id)
                } else {
                    locationTracker.markPermissionDenied()
                }
            } else {
                stopLiveLocationTrackingOnly(SosLocationStatus.IDLE)
            }
        }
    }

    fun stopLocationSharing() {
        localFormState.update { it.copy(locationConsentGiven = false) }
        stopLiveLocationTrackingOnly(SosLocationStatus.IDLE)
    }

    fun resolveActiveSos() {
        val activeId = localFormState.value.activeSosRequestId ?: return
        stopLiveLocationTrackingOnly(SosLocationStatus.IDLE)
        localFormState.update { it.copy(activeSosRequestId = null) }
        viewModelScope.launch {
            sosRepository.resolveSosRequest(activeId)
        }
    }

    fun cancelActiveSos() {
        val activeId = localFormState.value.activeSosRequestId ?: return
        stopLiveLocationTrackingOnly(SosLocationStatus.IDLE)
        localFormState.update { it.copy(activeSosRequestId = null) }
        viewModelScope.launch {
            sosRepository.cancelSosRequest(activeId)
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
        if (localFormState.value.activeSosRequestId == id) {
            stopLiveLocationTrackingOnly(SosLocationStatus.IDLE)
            localFormState.update { it.copy(activeSosRequestId = null) }
        }
        viewModelScope.launch { sosRepository.deleteRequest(id) }
    }

    private fun startPeriodicLocationUpdates(requestId: Int) {
        periodicLocationJob?.cancel()
        locationTracker.startTracking()
        periodicLocationJob = viewModelScope.launch {
            launch {
                locationTracker.currentLocation.collectLatest { snapshot ->
                    val form = localFormState.value
                    if (snapshot != null && form.activeSosRequestId == requestId && form.locationConsentGiven) {
                        syncActiveLocationToDb(requestId, snapshot)
                    }
                }
            }
            while (isActive) {
                val form = localFormState.value
                if (form.activeSosRequestId != requestId || !form.locationConsentGiven) break
                val latest = locationTracker.refreshSnapshot()
                if (latest != null) {
                    syncActiveLocationToDb(requestId, latest)
                }
                delay(10_000L)
            }
        }
    }

    private suspend fun syncActiveLocationToDb(requestId: Int, snapshot: EmergencyLocationSnapshot) {
        val latestUi = uiState.value.copy(
            liveLocation = snapshot,
            locationConsentGiven = true
        )
        sosRepository.updateSosLocationAndMessage(
            id = requestId,
            latitude = snapshot.latitude,
            longitude = snapshot.longitude,
            locationConsentGiven = true,
            updatedMessage = latestUi.buildPreparedSmsText()
        )
    }

    private fun stopLiveLocationTrackingOnly(nextStatus: SosLocationStatus) {
        periodicLocationJob?.cancel()
        periodicLocationJob = null
        locationTracker.stopTracking(nextStatus)
    }

    override fun onCleared() {
        super.onCleared()
        stopLiveLocationTrackingOnly(SosLocationStatus.IDLE)
    }

    private data class SosFormState(
        val crisisType: CrisisType = CrisisType.EARTHQUAKE,
        val emergencyType: String = "درخواست امداد فوری",
        val addressNote: String = "",
        val customNote: String = "",
        val deselectedContactIds: Set<String> = emptySet(),
        val locationConsentGiven: Boolean = false,
        val hasPermission: Boolean = false,
        val activeSosRequestId: Int? = null,
        val lastCreatedRequest: SosRequestEntity? = null
    )
}
