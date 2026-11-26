package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hamyar_prefs")

data class UserSettings(
    val onboardingCompleted: Boolean = false,
    val userName: String = "",
    val userCity: String = "تهران",
    val bloodType: String = "نامشخص",
    val medicalNotes: String = "",
    val highContrastMode: Boolean = false,
    val largeTextMode: Boolean = false,
    val darkModeEnabled: Boolean = false,
    val locationConsentAllowed: Boolean = false,
    val lastOfflineSyncMillis: Long = 0L
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_CITY = stringPreferencesKey("user_city")
        val BLOOD_TYPE = stringPreferencesKey("blood_type")
        val MEDICAL_NOTES = stringPreferencesKey("medical_notes")
        val HIGH_CONTRAST = booleanPreferencesKey("high_contrast_mode")
        val LARGE_TEXT = booleanPreferencesKey("large_text_mode")
        val DARK_MODE = booleanPreferencesKey("dark_mode_enabled")
        val LOCATION_CONSENT = booleanPreferencesKey("location_consent_allowed")
        val LAST_OFFLINE_SYNC = longPreferencesKey("last_offline_sync_millis")
    }

    val userSettings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            userName = prefs[Keys.USER_NAME] ?: "",
            userCity = prefs[Keys.USER_CITY] ?: "تهران",
            bloodType = prefs[Keys.BLOOD_TYPE] ?: "نامشخص",
            medicalNotes = prefs[Keys.MEDICAL_NOTES] ?: "",
            highContrastMode = prefs[Keys.HIGH_CONTRAST] ?: false,
            largeTextMode = prefs[Keys.LARGE_TEXT] ?: false,
            darkModeEnabled = prefs[Keys.DARK_MODE] ?: false,
            locationConsentAllowed = prefs[Keys.LOCATION_CONSENT] ?: false,
            lastOfflineSyncMillis = prefs[Keys.LAST_OFFLINE_SYNC] ?: 0L
        )
    }

    suspend fun completeOnboarding(userName: String, userCity: String, locationConsent: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = true
            prefs[Keys.USER_NAME] = userName.trim()
            if (userCity.isNotBlank()) prefs[Keys.USER_CITY] = userCity.trim()
            prefs[Keys.LOCATION_CONSENT] = locationConsent
            if ((prefs[Keys.LAST_OFFLINE_SYNC] ?: 0L) == 0L) {
                prefs[Keys.LAST_OFFLINE_SYNC] = System.currentTimeMillis()
            }
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun updateProfile(userName: String, userCity: String, bloodType: String, medicalNotes: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = userName.trim()
            prefs[Keys.USER_CITY] = userCity.trim()
            prefs[Keys.BLOOD_TYPE] = bloodType.trim()
            prefs[Keys.MEDICAL_NOTES] = medicalNotes.trim()
        }
    }

    suspend fun updateAccessibility(highContrast: Boolean, largeText: Boolean, darkMode: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.HIGH_CONTRAST] = highContrast
            prefs[Keys.LARGE_TEXT] = largeText
            prefs[Keys.DARK_MODE] = darkMode
        }
    }

    suspend fun setLocationConsent(allowed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LOCATION_CONSENT] = allowed
        }
    }

    suspend fun updateOfflineSyncTimestamp(timestamp: Long = System.currentTimeMillis()) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_OFFLINE_SYNC] = timestamp
        }
    }
}
