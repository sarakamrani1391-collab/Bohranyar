package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.SosRequestEntity
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.SosRepository
import com.example.data.system.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SosUiState(
    val selectedEmergencyType: String = "درخواست امداد فوری",
    val addressNote: String = "",
    val customNote: String = "",
    val isOnline: Boolean = false,
    val userSettings: UserSettings = UserSettings(),
    val history: List<SosRequestEntity> = emptyList(),
    val lastCreatedRequest: SosRequestEntity? = null
) {
    fun buildPreparedSmsText(): String {
        val namePart = userSettings.userName.takeIf { it.isNotBlank() }?.let { "نام: $it\n" } ?: ""
        val cityPart = userSettings.userCity.takeIf { it.isNotBlank() }?.let { "شهر: $it\n" } ?: ""
        val bloodPart = userSettings.bloodType.takeIf { it.isNotBlank() && it != "نامشخص" }
            ?.let { "گروه خونی: $it\n" } ?: ""
        val addressPart = addressNote.trim().takeIf { it.isNotBlank() }?.let { "نشانی تقریبی: $it\n" } ?: ""
        val notePart = customNote.trim().takeIf { it.isNotBlank() }?.let { "توضیحات: $it\n" } ?: ""
        return "درخواست کمک اضطراری - $selectedEmergencyType\n$namePart$cityPart$bloodPart$addressPart$notePart"
    }
}

class SosViewModel(
    private val sosRepository: SosRepository,
    preferencesRepository: UserPreferencesRepository,
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
            addressNote = form.addressNote,
            customNote = form.customNote,
            isOnline = online,
            userSettings = settings,
            history = history,
            lastCreatedRequest = form.lastCreatedRequest
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SosUiState())

    fun selectEmergencyType(type: String) {
        localFormState.update { it.copy(emergencyType = type) }
    }

    fun updateAddressNote(address: String) {
        localFormState.update { it.copy(addressNote = address) }
    }

    fun updateCustomNote(note: String) {
        localFormState.update { it.copy(customNote = note) }
    }

    fun submitSosRequest() {
        viewModelScope.launch {
            val state = uiState.value
            val created = sosRepository.createSosRequest(
                emergencyType = state.selectedEmergencyType,
                message = state.buildPreparedSmsText(),
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

    private data class SosFormState(
        val emergencyType: String = "درخواست امداد فوری",
        val addressNote: String = "",
        val customNote: String = "",
        val lastCreatedRequest: SosRequestEntity? = null
    )
}
