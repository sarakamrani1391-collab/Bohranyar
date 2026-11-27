package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.MavaSettingsEntity
import com.example.data.local.entity.MediaCategory
import com.example.data.local.entity.OfflineMediaEntity
import com.example.data.local.entity.QuizProgressEntity
import com.example.data.model.MavaCartoonEpisode
import com.example.data.model.MavaDiscoveryItem
import com.example.data.model.MavaStory
import com.example.data.model.QuizQuestion
import com.example.data.model.ScenarioItem
import com.example.data.model.ScenarioOption
import com.example.data.model.ScienceCategory
import com.example.data.model.ScienceTopic
import com.example.data.model.VirtualLabExperiment
import com.example.data.model.WhyQuestion
import com.example.data.repository.MavaRepository
import com.example.data.repository.OfflineMediaRepository
import com.example.data.repository.ScienceAndLabRepository
import com.example.data.repository.TrainingRepository
import com.example.data.seed.MavaContentSeed
import com.example.data.system.MavaSoundEngine
import com.example.data.system.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScienceUiState(
    val searchQuery: String = "",
    val selectedCategory: ScienceCategory? = null,
    val topics: List<ScienceTopic> = emptyList(),
    val whyQuestions: List<WhyQuestion> = emptyList(),
    val progressMap: Map<String, QuizProgressEntity> = emptyMap(),
    val selectedQuizAnswers: Map<String, Int> = emptyMap()
)

class ScienceViewModel(
    private val scienceRepository: ScienceAndLabRepository,
    private val trainingRepository: TrainingRepository
) : ViewModel() {

    private val filterState = MutableStateFlow(ScienceFilterState())

    val uiState: StateFlow<ScienceUiState> = combine(
        filterState,
        trainingRepository.observeQuizProgress()
    ) { filter, progress ->
        val baseTopics = scienceRepository.getTopicsByCategory(filter.category)
        val filtered = if (filter.query.isBlank()) {
            baseTopics
        } else {
            val q = filter.query.trim()
            baseTopics.filter {
                it.title.contains(q, ignoreCase = true) ||
                    it.gradeInfo.contains(q, ignoreCase = true) ||
                    it.simpleExplanation.contains(q, ignoreCase = true)
            }
        }
        ScienceUiState(
            searchQuery = filter.query,
            selectedCategory = filter.category,
            topics = filtered,
            whyQuestions = scienceRepository.getWhyQuestions(),
            progressMap = progress.associateBy { it.topicId },
            selectedQuizAnswers = filter.quizAnswers
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ScienceUiState(
            topics = scienceRepository.getAllTopics(),
            whyQuestions = scienceRepository.getWhyQuestions()
        )
    )

    fun updateSearch(query: String) {
        filterState.update { it.copy(query = query) }
    }

    fun selectCategory(category: ScienceCategory?) {
        filterState.update { it.copy(category = category) }
    }

    fun getTopicById(id: String): ScienceTopic? = scienceRepository.getTopicById(id)

    fun getQuestionsForTopic(topicId: String): List<QuizQuestion> =
        trainingRepository.getQuestionsForTopic(topicId)

    fun answerInlineQuiz(question: QuizQuestion, chosenIndex: Int) {
        if (filterState.value.quizAnswers.containsKey(question.id)) return
        filterState.update {
            it.copy(quizAnswers = it.quizAnswers + (question.id to chosenIndex))
        }
        val score = if (chosenIndex == question.correctIndex) 1 else 0
        val prevBest = uiState.value.progressMap[question.topicId]?.bestScore ?: 0
        viewModelScope.launch {
            trainingRepository.saveQuizResult(
                topicId = question.topicId,
                score = score,
                total = 1,
                previousBest = prevBest
            )
        }
    }

    fun resetInlineQuiz(questionId: String) {
        filterState.update {
            it.copy(quizAnswers = it.quizAnswers - questionId)
        }
    }

    private data class ScienceFilterState(
        val query: String = "",
        val category: ScienceCategory? = null,
        val quizAnswers: Map<String, Int> = emptyMap()
    )
}

data class ScenarioLabUiState(
    val scenarios: List<ScenarioItem> = emptyList(),
    val activeScenarioIndex: Int = 0,
    val selectedOption: ScenarioOption? = null,
    val completedScenarioIds: Set<String> = emptySet()
)

class ScenarioLabViewModel(
    scienceRepository: ScienceAndLabRepository
) : ViewModel() {

    private val allScenarios = scienceRepository.getScenarios()

    private val _uiState = MutableStateFlow(
        ScenarioLabUiState(scenarios = allScenarios)
    )
    val uiState: StateFlow<ScenarioLabUiState> = _uiState

    fun selectScenario(index: Int) {
        _uiState.update {
            it.copy(
                activeScenarioIndex = index.coerceIn(0, (allScenarios.size - 1).coerceAtLeast(0)),
                selectedOption = null
            )
        }
    }

    fun chooseOption(option: ScenarioOption) {
        _uiState.update { state ->
            val currentScenario = state.scenarios.getOrNull(state.activeScenarioIndex)
            val updatedCompleted = if (option.isSafeAndCorrect && currentScenario != null) {
                state.completedScenarioIds + currentScenario.id
            } else {
                state.completedScenarioIds
            }
            state.copy(
                selectedOption = option,
                completedScenarioIds = updatedCompleted
            )
        }
    }

    fun retryCurrent() {
        _uiState.update { it.copy(selectedOption = null) }
    }
}

data class MavaUiState(
    val settings: MavaSettingsEntity = MavaSettingsEntity(),
    val stories: List<MavaStory> = MavaContentSeed.stories,
    val cartoons: List<MavaCartoonEpisode> = MavaContentSeed.cartoons,
    val discoveryItems: List<MavaDiscoveryItem> = MavaContentSeed.discoveryItems,
    val cartoonMediaMap: Map<String, OfflineMediaEntity> = emptyMap(),
    val isOnline: Boolean = false,
    val statusNote: String? = null
)

class MavaViewModel(
    private val mavaRepository: MavaRepository,
    private val mediaRepository: OfflineMediaRepository,
    private val networkMonitor: NetworkMonitor,
    private val appContext: Context
) : ViewModel() {

    private val statusMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MavaUiState> = combine(
        mavaRepository.observeSettings(),
        mediaRepository.observeByCategory(MediaCategory.MAVA_CARTOON),
        networkMonitor.isOnline,
        statusMessage
    ) { settings, mediaList, online, note ->
        MavaUiState(
            settings = settings,
            cartoonMediaMap = mediaList.associateBy { it.mediaId },
            isOnline = online,
            statusNote = note
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MavaUiState())

    fun updateSettings(updated: MavaSettingsEntity) {
        viewModelScope.launch {
            mavaRepository.saveSettings(updated)
        }
    }

    fun playTone(freqHz: Double = 523.25) {
        MavaSoundEngine.playGentleChime(freqHz = freqHz, enabled = uiState.value.settings.soundEnabled)
    }

    fun playHappyTone() {
        MavaSoundEngine.playHappyMelody(enabled = uiState.value.settings.soundEnabled)
    }

    fun playLullabyStep(step: Int) {
        MavaSoundEngine.playCalmLullabyNote(step = step, enabled = uiState.value.settings.soundEnabled)
    }

    fun importLocalCartoonVideo(mediaId: String, uri: Uri) {
        viewModelScope.launch {
            val ok = mediaRepository.importLocalVideo(mediaId, MediaCategory.MAVA_CARTOON, uri)
            statusMessage.value = if (ok) {
                "فایل کارتون در حافظه آفلاین مأوا ذخیره شد."
            } else {
                "ذخیره فایل کارتون ناموفق بود."
            }
        }
    }

    fun removeOfflineCartoon(asset: OfflineMediaEntity) {
        viewModelScope.launch {
            mediaRepository.removeOfflineVideo(asset)
            statusMessage.value = "فایل آفلاین کارتون حذف شد."
        }
    }

    fun dismissNote() {
        statusMessage.value = null
    }
}
