package com.example.data.model

import androidx.annotation.RawRes
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
    val mediaId: String,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val summary: String,
    val steps: List<String>,
    val warningNote: String,
    val sourceNote: String,
    val lastReviewedDate: String,
    val relatedCrisis: CrisisType?,
    val relatedQuizTopicId: String
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

// Science & Crisis Encyclopedia Models
enum class ScienceCategory(val titleFa: String) {
    CHEMISTRY("شیمی و مواد"),
    PHYSICS("فیزیک"),
    BIOLOGY("زیست‌شناسی"),
    GEOLOGY_ENV("زمین‌شناسی و محیط زیست")
}

enum class ScienceDiagramType {
    COMBUSTION_CO,
    HEAT_TRANSFER,
    ELECTRIC_CIRCUIT,
    PRESSURE_FORCE,
    RESPIRATORY_HEMOGLOBIN,
    EARTH_SEISMIC,
    WATER_CYCLE_POLLUTION
}

data class ScienceTopic(
    val id: String,
    val category: ScienceCategory,
    val title: String,
    val gradeInfo: String,
    val chapterAlignment: String,
    val scientificDefinition: String,
    val simpleExplanation: String,
    val textbookConnection: String,
    val crisisApplication: String,
    val keyPoints: List<String>,
    val diagramType: ScienceDiagramType,
    val sourceNote: String,
    val lastReviewedDate: String,
    val quizTopicId: String
)

data class WhyQuestion(
    val id: String,
    val question: String,
    val shortAnswer: String,
    val scientificExplanation: String,
    val crisisTakeaway: String,
    val diagramType: ScienceDiagramType,
    val quizQuestion: QuizQuestion
)

// Virtual Lab Models
enum class LabSimulationType {
    HEAT_TRANSFER,
    COMBUSTION,
    SEISMIC_WAVES,
    ELECTRIC_CIRCUIT,
    WATER_FILTRATION,
    STRUCTURE_STABILITY
}

data class VirtualLabExperiment(
    val id: String,
    val type: LabSimulationType,
    val title: String,
    val subtitle: String,
    val scientificExplanation: String,
    val safetyDisclaimer: String,
    val gradeReference: String
)

// Scenario Lab Models
data class ScenarioOption(
    val id: String,
    val label: String,
    val isSafeAndCorrect: Boolean,
    val outcomeTitle: String,
    val scientificAndSafetyReason: String,
    val correctiveAdvice: String
)

data class ScenarioItem(
    val id: String,
    val title: String,
    val situationDescription: String,
    val commonMistakes: List<String>,
    val options: List<ScenarioOption>
)

// Ma'va (Toddler Sanctuary) Models
enum class MavaCharacterId(
    val displayNameFa: String,
    val speciesFa: String,
    val traitFa: String,
    val voiceStyleFa: String
) {
    PUFI("پوفی", "خرگوش کنجکاو", "مهربان، بازیگوش و دوست‌داشتنی", "صدای زنگوله‌ای نرم و شاد"),
    TOTO("توتو", "لاک‌پشت آرام", "صبور، آرام‌بخش و مراقب دوستانش", "نوای بم و آرام‌بخش طبیعت"),
    BOMBO("بومبو", "خرس کوچولو", "بغل‌کردنی، گرم و حامی کوچولوها", "صدای گرم و ملایم ماریمبا"),
    FOFO("فوفو", "روباه باهوش", "پرسشگر، کنجکاو و ماجراجوی مهربان", "آوای زنگ ملایم و کنجکاوانه")
}

enum class MavaEmotionType(val titleFa: String, val simpleMessageFa: String) {
    HAPPY("شادی", "وقتی لبخند می‌زنیم و با دوستان بازی می‌کنیم، دلمان شاد است!"),
    SAD("ناراحتی", "اشکالی ندارد گاهی ناراحت شویم؛ بغل مامان یا بابا آراممان می‌کند."),
    SCARED("ترس", "وقتی می‌ترسیم، دست بزرگتر مهربان را می‌گیریم و نفس عمیق می‌کشیم."),
    CALM("آرامش", "نفس عمیق مثل باد ملایم؛ همه‌چیز آرام و امن است.")
}

data class MavaStory(
    val id: String,
    val title: String,
    val themeFa: String,
    val mainCharacter: MavaCharacterId,
    val pages: List<MavaStoryPage>
)

data class MavaStoryPage(
    val character: MavaCharacterId,
    val emotion: MavaEmotionType,
    val simpleLine: String
)

data class MavaCartoonEpisode(
    val id: String,
    val mediaId: String,
    val title: String,
    val character: MavaCharacterId,
    val positiveMessage: String,
    val durationLabel: String,
    val animatedFrames: List<MavaStoryPage>,
    @RawRes val bundledVideoResId: Int? = null
)

data class MavaDiscoveryItem(
    val id: String,
    val groupFa: String,
    val titleFa: String,
    val detailFa: String,
    val character: MavaCharacterId,
    val toneHz: Double
)
