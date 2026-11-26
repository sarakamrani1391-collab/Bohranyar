package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class CrisisType(
    val id: String,
    val titleFa: String,
    val shortSummaryFa: String,
    val categoryLabelFa: String
) {
    EARTHQUAKE(
        id = "earthquake",
        titleFa = "زلزله",
        shortSummaryFa = "پناه‌گیری فوری، قطع شریان‌ها و آمادگی برای پس‌لرزه",
        categoryLabelFa = "طبیعی / لرزه‌ای"
    ),
    FLOOD(
        id = "flood",
        titleFa = "سیل",
        shortSummaryFa = "تخلیه به نقاط مرتفع و پرهیز از عبور در جریان آب",
        categoryLabelFa = "جوی / آبی"
    ),
    FIRE(
        id = "fire",
        titleFa = "آتش‌سوزی",
        shortSummaryFa = "خروج ایمن از مسیر کم‌دود و تماس فوری با ۱۲۵",
        categoryLabelFa = "حریق"
    ),
    STORM(
        id = "storm",
        titleFa = "طوفان",
        shortSummaryFa = "ایمن‌سازی پنجره‌ها، دوری از سازه‌های ناپایدار و کابل‌ها",
        categoryLabelFa = "جوی / باد شدید"
    ),
    SNOW_BLIZZARD(
        id = "snow_blizzard",
        titleFa = "برف و کولاک",
        shortSummaryFa = "پیشگیری از یخ‌زدگی، گرمایش ایمن و ماندن در خودرو یا پناهگاه",
        categoryLabelFa = "سرما / جاده‌ای"
    ),
    POWER_OUTAGE(
        id = "power_outage",
        titleFa = "قطعی برق",
        shortSummaryFa = "مدیریت باتری، نگهداری غذا و روشنایی ایمن بدون شمع",
        categoryLabelFa = "زیرساختی"
    ),
    AIR_POLLUTION(
        id = "air_pollution",
        titleFa = "آلودگی هوا",
        shortSummaryFa = "کاهش مواجهه، استفاده از ماسک استاندارد و محافظت از گروه‌های حساس",
        categoryLabelFa = "زیست‌محیطی"
    ),
    URBAN_INCIDENT(
        id = "urban_incident",
        titleFa = "حوادث شهری",
        shortSummaryFa = "مدیریت نشت گاز، ریزش سازه، حوادث ترافیکی و انسداد معابر",
        categoryLabelFa = "شهری / زیرساخت"
    );

    companion object {
        fun fromId(id: String): CrisisType = entries.firstOrNull { it.id == id } ?: EARTHQUAKE
    }
}

data class CrisisGuide(
    val type: CrisisType,
    val beforeActions: List<String>,
    val duringActions: List<String>,
    val afterActions: List<String>,
    val commonMistakes: List<String>,
    val safetyWarnings: List<String>,
    val recommendedTrainingId: String
)

data class EmergencyContactNumber(
    val id: String,
    val title: String,
    val number: String,
    val description: String,
    val isPrimary: Boolean = false
)

data class TrainingLesson(
    val id: String,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val summary: String,
    val steps: List<String>,
    val warningNote: String,
    val sourceNote: String,
    val relatedCrisis: CrisisType?
)

data class QuizQuestion(
    val id: String,
    val topicId: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class QuizTopic(
    val id: String,
    val title: String,
    val subtitle: String,
    val relatedCrisis: CrisisType?
)

data class SampleAdvisory(
    val id: String,
    val title: String,
    val body: String,
    val levelLabel: String,
    val isDemoData: Boolean = true
)
