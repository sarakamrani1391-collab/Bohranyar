package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.EmergencyKitItemEntity
import com.example.data.local.entity.FamilyMemberEntity
import com.example.data.local.entity.FamilyPlanEntity
import com.example.data.local.entity.FamilySafetyStatus
import com.example.data.local.entity.KitCategory
import com.example.data.local.entity.MediaCategory
import com.example.data.local.entity.OfflineMediaEntity
import com.example.data.local.entity.QuizProgressEntity
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizTopic
import com.example.data.model.TrainingLesson
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.FamilyRepository
import com.example.data.repository.KitRepository
import com.example.data.repository.OfflineMediaRepository
import com.example.data.repository.SosRepository
import com.example.data.repository.TrainingRepository
import com.example.data.system.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FamilyUiState(
    val members: List<FamilyMemberEntity> = emptyList(),
    val plan: FamilyPlanEntity = FamilyPlanEntity()
)

class FamilyViewModel(
    private val familyRepository: FamilyRepository
) : ViewModel() {

    val uiState: StateFlow<FamilyUiState> = combine(
        familyRepository.observeMembers(),
        familyRepository.observePlan()
    ) { members, plan ->
        FamilyUiState(
            members = members,
            plan = plan ?: FamilyPlanEntity()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FamilyUiState())

    fun addMember(name: String, relation: String, phone: String, bloodType: String, medicalNotes: String) {
        if (name.isBlank() || phone.isBlank()) return
        viewModelScope.launch {
            familyRepository.addMember(name, relation, phone, bloodType, medicalNotes)
        }
    }

    fun updateSafetyStatus(id: Int, status: FamilySafetyStatus) {
        viewModelScope.launch {
            familyRepository.updateSafetyStatus(id, status)
        }
    }

    fun deleteMember(id: Int) {
        viewModelScope.launch {
            familyRepository.deleteMember(id)
        }
    }

    fun saveFamilyPlan(
        primaryPoint: String,
        secondaryPoint: String,
        outOfCityName: String,
        outOfCityPhone: String,
        notes: String
    ) {
        viewModelScope.launch {
            familyRepository.savePlan(primaryPoint, secondaryPoint, outOfCityName, outOfCityPhone, notes)
        }
    }
}

data class KitUiState(
    val items: List<EmergencyKitItemEntity> = emptyList(),
    val selectedCategory: KitCategory? = null,
    val onlyUnprepared: Boolean = false
) {
    val preparedCount: Int get() = items.count { it.isPrepared }
    val totalCount: Int get() = items.size
    val progressRatio: Float get() = if (items.isEmpty()) 0f else preparedCount.toFloat() / items.size
    val visibleItems: List<EmergencyKitItemEntity>
        get() = items.filter { item ->
            (selectedCategory == null || item.category == selectedCategory) &&
                (!onlyUnprepared || !item.isPrepared)
        }
}

class KitViewModel(
    private val kitRepository: KitRepository
) : ViewModel() {

    private val filterState = MutableStateFlow(KitFilterState())

    val uiState: StateFlow<KitUiState> = combine(
        kitRepository.observeAllItems(),
        filterState
    ) { items, filter ->
        KitUiState(
            items = items,
            selectedCategory = filter.category,
            onlyUnprepared = filter.onlyUnprepared
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KitUiState())

    fun selectCategory(category: KitCategory?) {
        filterState.update { it.copy(category = category) }
    }

    fun toggleOnlyUnprepared() {
        filterState.update { it.copy(onlyUnprepared = !it.onlyUnprepared) }
    }

    fun toggleItemPrepared(id: Int, prepared: Boolean) {
        viewModelScope.launch { kitRepository.togglePrepared(id, prepared) }
    }

    fun addCustomItem(name: String, category: KitCategory, quantity: String) {
        if (name.isBlank()) return
        viewModelScope.launch { kitRepository.addCustomItem(name, category, quantity) }
    }

    fun deleteItem(id: Int) {
        viewModelScope.launch { kitRepository.deleteItem(id) }
    }

    private data class KitFilterState(
        val category: KitCategory? = null,
        val onlyUnprepared: Boolean = false
    )
}

data class TrainingUiState(
    val lessons: List<TrainingLesson> = emptyList(),
    val mediaMap: Map<String, OfflineMediaEntity> = emptyMap(),
    val quizTopics: List<QuizTopic> = emptyList(),
    val progressMap: Map<String, QuizProgressEntity> = emptyMap(),
    val isOnline: Boolean = false,
    val mediaBannerMessage: String? = null,
    val activeQuizTopic: QuizTopic? = null,
    val activeQuestions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val correctAnswersCount: Int = 0,
    val isQuizFinished: Boolean = false
)

class TrainingViewModel(
    private val trainingRepository: TrainingRepository,
    private val mediaRepository: OfflineMediaRepository,
    private val networkMonitor: NetworkMonitor,
    private val appContext: Context
) : ViewModel() {

    private val quizSession = MutableStateFlow(QuizSessionState())
    private val mediaBanner = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TrainingUiState> = combine(
        trainingRepository.observeQuizProgress(),
        mediaRepository.observeByCategory(MediaCategory.FIRST_AID),
        networkMonitor.isOnline,
        quizSession,
        mediaBanner
    ) { progressList, mediaList, online, session, banner ->
        TrainingUiState(
            lessons = trainingRepository.getLessons(),
            mediaMap = mediaList.associateBy { it.mediaId },
            quizTopics = trainingRepository.getQuizTopics(),
            progressMap = progressList.associateBy { it.topicId },
            isOnline = online,
            mediaBannerMessage = banner,
            activeQuizTopic = session.topic,
            activeQuestions = session.questions,
            currentQuestionIndex = session.currentIndex,
            selectedOptionIndex = session.selectedOptionIndex,
            correctAnswersCount = session.correctCount,
            isQuizFinished = session.isFinished
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TrainingUiState(
            lessons = trainingRepository.getLessons(),
            quizTopics = trainingRepository.getQuizTopics()
        )
    )

    fun getLessonById(id: String): TrainingLesson? = trainingRepository.getLessonById(id)

    fun importLocalLessonVideo(mediaId: String, uri: Uri) {
        viewModelScope.launch {
            val ok = mediaRepository.importLocalVideo(mediaId, MediaCategory.FIRST_AID, uri)
            mediaBanner.value = if (ok) {
                "فایل ویدئویی با موفقیت در حافظه آفلاین ذخیره شد و آماده پخش است."
            } else {
                "امکان خواندن فایل ویدئویی انتخاب‌شده وجود نداشت."
            }
        }
    }

    fun downloadLessonVideo(asset: OfflineMediaEntity) {
        viewModelScope.launch {
            if (asset.streamUrl.isNullOrBlank()) {
                mediaBanner.value = "منبع ویدئویی آنلاین این درس هنوز متصل نشده است؛ می‌توانید فایل ویدئویی محلی خودتان را بارگذاری کنید."
                return@launch
            }
            if (!networkMonitor.isOnline.value) {
                mediaBanner.value = "برای دانلود ویدئو اتصال اینترنت لازم است."
                return@launch
            }
            val ok = mediaRepository.downloadFromSource(asset)
            mediaBanner.value = if (ok) "ویدئو برای مشاهده آفلاین دانلود شد." else "دانلود ویدئو ناموفق بود."
        }
    }

    fun removeOfflineVideo(asset: OfflineMediaEntity) {
        viewModelScope.launch {
            mediaRepository.removeOfflineVideo(asset)
            mediaBanner.value = "فایل ویدئویی آفلاین حذف شد."
        }
    }

    fun dismissMediaBanner() {
        mediaBanner.value = null
    }

    fun startQuizById(topicId: String) {
        val topic = trainingRepository.getQuizTopics().firstOrNull { it.id == topicId } ?: return
        startQuiz(topic)
    }

    fun startQuiz(topic: QuizTopic) {
        val questions = trainingRepository.getQuestionsForTopic(topic.id)
        quizSession.value = QuizSessionState(
            topic = topic,
            questions = questions,
            currentIndex = 0,
            selectedOptionIndex = null,
            correctCount = 0,
            isFinished = false
        )
    }

    fun selectAnswer(optionIndex: Int) {
        val current = quizSession.value
        if (current.selectedOptionIndex != null || current.isFinished) return
        val question = current.questions.getOrNull(current.currentIndex) ?: return
        val isCorrect = optionIndex == question.correctIndex
        quizSession.value = current.copy(
            selectedOptionIndex = optionIndex,
            correctCount = if (isCorrect) current.correctCount + 1 else current.correctCount
        )
    }

    fun nextQuestion() {
        val current = quizSession.value
        val nextIndex = current.currentIndex + 1
        if (nextIndex >= current.questions.size) {
            quizSession.value = current.copy(isFinished = true)
            current.topic?.let { topic ->
                val prevBest = uiState.value.progressMap[topic.id]?.bestScore ?: 0
                viewModelScope.launch {
                    trainingRepository.saveQuizResult(
                        topicId = topic.id,
                        score = current.correctCount,
                        total = current.questions.size,
                        previousBest = prevBest
                    )
                }
            }
        } else {
            quizSession.value = current.copy(
                currentIndex = nextIndex,
                selectedOptionIndex = null
            )
        }
    }

    fun closeQuiz() {
        quizSession.value = QuizSessionState()
    }

    private data class QuizSessionState(
        val topic: QuizTopic? = null,
        val questions: List<QuizQuestion> = emptyList(),
        val currentIndex: Int = 0,
        val selectedOptionIndex: Int? = null,
        val correctCount: Int = 0,
        val isFinished: Boolean = false
    )
}

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val isOnline: Boolean = false,
    val statusMessage: String? = null
)

class SettingsViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    private val sosRepository: SosRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val messageFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.userSettings,
        networkMonitor.isOnline,
        messageFlow
    ) { settings, online, msg ->
        SettingsUiState(settings = settings, isOnline = online, statusMessage = msg)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun saveProfile(name: String, city: String, bloodType: String, medicalNotes: String) {
        viewModelScope.launch {
            preferencesRepository.updateProfile(name, city, bloodType, medicalNotes)
            messageFlow.value = "اطلاعات پروفایل اضطراری در حافظه امن دستگاه ذخیره شد."
        }
    }

    fun updateAccessibility(highContrast: Boolean, largeText: Boolean, darkMode: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateAccessibility(highContrast, largeText, darkMode)
        }
    }

    fun refreshOfflinePackage() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            preferencesRepository.updateOfflineSyncTimestamp(now)
            if (networkMonitor.isOnline.value) {
                sosRepository.syncQueuedRequests()
            }
            messageFlow.value = "بسته آفلاین راهنماها، دانشنامه و شماره‌ها در حافظه داخلی به‌روزرسانی شد."
        }
    }

    fun replayOnboarding() {
        viewModelScope.launch {
            preferencesRepository.setOnboardingCompleted(false)
        }
    }

    fun dismissMessage() {
        messageFlow.value = null
    }
}
