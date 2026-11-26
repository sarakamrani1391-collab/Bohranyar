package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.EmergencyKitItemEntity
import com.example.data.local.entity.FamilyMemberEntity
import com.example.data.local.entity.FamilyPlanEntity
import com.example.data.local.entity.FamilySafetyStatus
import com.example.data.local.entity.KitCategory
import com.example.data.local.entity.QuizProgressEntity
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizTopic
import com.example.data.model.TrainingLesson
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.FamilyRepository
import com.example.data.repository.KitRepository
import com.example.data.repository.MapRepository
import com.example.data.repository.ReportRepository
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
    val quizTopics: List<QuizTopic> = emptyList(),
    val progressMap: Map<String, QuizProgressEntity> = emptyMap(),
    val activeQuizTopic: QuizTopic? = null,
    val activeQuestions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val correctAnswersCount: Int = 0,
    val isQuizFinished: Boolean = false
)

class TrainingViewModel(
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val quizSession = MutableStateFlow(QuizSessionState())

    val uiState: StateFlow<TrainingUiState> = combine(
        trainingRepository.observeQuizProgress(),
        quizSession
    ) { progressList, session ->
        TrainingUiState(
            lessons = trainingRepository.getLessons(),
            quizTopics = trainingRepository.getQuizTopics(),
            progressMap = progressList.associateBy { it.topicId },
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
    private val mapRepository: MapRepository,
    private val sosRepository: SosRepository,
    private val reportRepository: ReportRepository,
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
            mapRepository.refreshOfflineCacheTimestamp(now)
            preferencesRepository.updateOfflineSyncTimestamp(now)
            if (networkMonitor.isOnline.value) {
                sosRepository.syncQueuedRequests()
                reportRepository.syncOfflineReports()
            }
            messageFlow.value = "بسته آفلاین راهنماها، شماره‌ها و نقشه ذخیره شده به‌روزرسانی شد."
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
