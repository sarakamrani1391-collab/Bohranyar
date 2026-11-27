package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class KitCategory(val titleFa: String) {
    FOOD_WATER("غذایی و آب"),
    FIRST_AID("کمک‌های اولیه"),
    COMMUNICATION("ارتباطی و ابزار"),
    HYGIENE("بهداشتی و حفاظتی")
}

enum class FamilySafetyStatus(val titleFa: String) {
    SAFE("سالم و ایمن"),
    NEEDS_HELP("نیاز به کمک"),
    UNKNOWN("وضعیت نامشخص")
}

enum class SosStatus(val titleFa: String) {
    QUEUED_OFFLINE("در صف ارسال آفلاین"),
    SENT_SYNCED("آماده / ثبت‌شده آنلاین"),
    SHARED_BY_USER("ارسال‌شده از طریق پیامک/تماس")
}

enum class MediaCategory {
    FIRST_AID,
    MAVA_CARTOON
}

enum class MediaDownloadStatus(val titleFa: String) {
    NOT_LINKED("منبع رسمی هنوز متصل نشده"),
    NOT_DOWNLOADED("آماده دانلود"),
    DOWNLOADING("در حال دانلود..."),
    DOWNLOADED("ذخیره‌شده برای پخش آفلاین")
}

@Entity(tableName = "crisis_checklists")
data class CrisisChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val crisisId: String,
    val title: String,
    val isChecked: Boolean = false,
    val sortOrder: Int = 0
)

@Entity(tableName = "emergency_kit_items")
data class EmergencyKitItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: KitCategory,
    val quantity: String = "۱ عدد",
    val isPrepared: Boolean = false,
    val isCustom: Boolean = false,
    val expiryNote: String = ""
)

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val relation: String,
    val phone: String,
    val bloodType: String = "",
    val medicalNotes: String = "",
    val safetyStatus: FamilySafetyStatus = FamilySafetyStatus.UNKNOWN,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "family_plan")
data class FamilyPlanEntity(
    @PrimaryKey val id: Int = 1,
    val primaryMeetingPoint: String = "",
    val secondaryMeetingPoint: String = "",
    val outOfCityContactName: String = "",
    val outOfCityContactPhone: String = "",
    val emergencyNotes: String = "",
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "sos_requests")
data class SosRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val emergencyType: String,
    val message: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationConsentGiven: Boolean = false,
    val status: SosStatus = SosStatus.QUEUED_OFFLINE,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_progress")
data class QuizProgressEntity(
    @PrimaryKey val topicId: String,
    val bestScore: Int,
    val totalQuestions: Int,
    val completedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_media_assets")
data class OfflineMediaEntity(
    @PrimaryKey val mediaId: String,
    val category: MediaCategory,
    val title: String,
    val description: String,
    val sourceAttribution: String,
    val streamUrl: String?,
    val localFilePath: String?,
    val status: MediaDownloadStatus,
    val durationLabel: String,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "mava_settings")
data class MavaSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val ageGroup: String = "1_3",
    val soundEnabled: Boolean = true,
    val dailyLimitMinutes: Int = 20,
    val gamesEnabled: Boolean = true,
    val storiesEnabled: Boolean = true,
    val calmEnabled: Boolean = true,
    val emotionsEnabled: Boolean = true,
    val cartoonsEnabled: Boolean = true,
    val discoveryEnabled: Boolean = true,
    val updatedAtMillis: Long = System.currentTimeMillis()
)
