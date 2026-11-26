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

enum class UrbanReportCategory(val titleFa: String) {
    ROAD_BLOCKAGE("انسداد مسیر"),
    FLOODING("آب‌گرفتگی"),
    INFRASTRUCTURE("خرابی زیرساخت"),
    ENVIRONMENTAL("خطر محیطی")
}

enum class UrbanReportStatus(val titleFa: String) {
    QUEUED_OFFLINE("ذخیره آفلاین (در انتظار اتصال)"),
    SUBMITTED_UNVERIFIED("ثبت‌شده (تأییدنشده)"),
    VERIFIED("تأییدشده")
}

enum class ShelterType(val titleFa: String) {
    HOSPITAL("بیمارستان و مرکز درمانی"),
    RELIEF_CENTER("مرکز امدادی / آتش‌نشانی"),
    SAFE_ZONE("نقطه امن و اسکان باز"),
    FAMILY_POINT("نقطه امن شخصی / خانواده")
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

@Entity(tableName = "urban_reports")
data class UrbanReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: UrbanReportCategory,
    val title: String,
    val description: String,
    val imagePath: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationConsentGiven: Boolean = false,
    val status: UrbanReportStatus = UrbanReportStatus.QUEUED_OFFLINE,
    val isDemoData: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "shelter_points")
data class ShelterPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: ShelterType,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val capacityNote: String = "",
    val phone: String = "",
    val isVerified: Boolean = false,
    val isDemoData: Boolean = true,
    val cachedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_progress")
data class QuizProgressEntity(
    @PrimaryKey val topicId: String,
    val bestScore: Int,
    val totalQuestions: Int,
    val completedAtMillis: Long = System.currentTimeMillis()
)
