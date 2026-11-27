package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.AppContainer
import com.example.CrisisApplication
import com.example.data.local.entity.CrisisChecklistEntity
import com.example.data.model.CrisisGuide
import com.example.data.model.CrisisType
import com.example.data.model.EmergencyContactNumber
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.CrisisRepository
import com.example.data.repository.FamilyRepository
import com.example.data.repository.KitRepository
import com.example.data.repository.SosRepository
import com.example.data.repository.TrainingRepository
import com.example.data.system.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun CreationExtras.crisisApp(): CrisisApplication =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CrisisApplication

fun CreationExtras.appContainer(): AppContainer = crisisApp().container

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val c = appContainer()
            MainAppViewModel(c.preferencesRepository, c.networkMonitor)
        }
        initializer {
            val c = appContainer()
            DashboardViewModel(
                crisisRepository = c.crisisRepository,
                kitRepository = c.kitRepository,
                familyRepository = c.familyRepository,
                trainingRepository = c.trainingRepository,
                sosRepository = c.sosRepository,
                preferencesRepository = c.preferencesRepository,
                networkMonitor = c.networkMonitor
            )
        }
        initializer {
            val c = appContainer()
            CrisisGuideViewModel(c.crisisRepository)
        }
        initializer {
            val c = appContainer()
            SosViewModel(
                sosRepository = c.sosRepository,
                familyRepository = c.familyRepository,
                preferencesRepository = c.preferencesRepository,
                networkMonitor = c.networkMonitor,
                locationTracker = c.sosLocationTracker
            )
        }
        initializer {
            val c = appContainer()
            FamilyViewModel(c.familyRepository)
        }
        initializer {
            val c = appContainer()
            KitViewModel(c.kitRepository)
        }
        initializer {
            val c = appContainer()
            TrainingViewModel(
                trainingRepository = c.trainingRepository,
                mediaRepository = c.offlineMediaRepository,
                networkMonitor = c.networkMonitor,
                appContext = crisisApp()
            )
        }
        initializer {
            val c = appContainer()
            SettingsViewModel(
                preferencesRepository = c.preferencesRepository,
                sosRepository = c.sosRepository,
                networkMonitor = c.networkMonitor
            )
        }
        initializer {
            val c = appContainer()
            ScienceViewModel(
                scienceRepository = c.scienceAndLabRepository,
                trainingRepository = c.trainingRepository
            )
        }
        initializer {
            val c = appContainer()
            ScenarioLabViewModel(c.scienceAndLabRepository)
        }
        initializer {
            val c = appContainer()
            MavaViewModel(
                mavaRepository = c.mavaRepository,
                mediaRepository = c.offlineMediaRepository,
                networkMonitor = c.networkMonitor,
                appContext = crisisApp()
            )
        }
    }
}

class MainAppViewModel(
    private val preferencesRepository: UserPreferencesRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = preferencesRepository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline

    fun completeOnboarding(name: String, city: String) {
        viewModelScope.launch {
            preferencesRepository.completeOnboarding(name, city)
        }
    }
}

data class DashboardUiState(
    val userName: String = "",
    val userCity: String = "تهران",
    val isOnline: Boolean = false,
    val preparednessPercent: Int = 0,
    val kitPreparedCount: Int = 0,
    val kitTotalCount: Int = 0,
    val familyCount: Int = 0,
    val hasFamilyPlan: Boolean = false,
    val quizzesCompletedCount: Int = 0,
    val queuedSosCount: Int = 0,
    val lastOfflineSyncMillis: Long = 0L,
    val emergencyNumbers: List<EmergencyContactNumber> = emptyList()
)

class DashboardViewModel(
    private val crisisRepository: CrisisRepository,
    kitRepository: KitRepository,
    familyRepository: FamilyRepository,
    trainingRepository: TrainingRepository,
    sosRepository: SosRepository,
    preferencesRepository: UserPreferencesRepository,
    networkMonitor: NetworkMonitor
) : ViewModel() {

    private val baseStatsFlow = combine(
        kitRepository.observeAllItems(),
        crisisRepository.observeAllChecklists(),
        familyRepository.observeMembers(),
        familyRepository.observePlan(),
        trainingRepository.observeQuizProgress()
    ) { kitItems, checklists, familyMembers, plan, quizzes ->
        val kitRatio = if (kitItems.isEmpty()) 0f else kitItems.count { it.isPrepared }.toFloat() / kitItems.size
        val checklistRatio = if (checklists.isEmpty()) 0f else checklists.count { it.isChecked }.toFloat() / checklists.size
        val hasPlan = plan != null && (plan.primaryMeetingPoint.isNotBlank() || plan.outOfCityContactPhone.isNotBlank())
        val familyRatio = when {
            familyMembers.isNotEmpty() && hasPlan -> 1f
            familyMembers.isNotEmpty() || hasPlan -> 0.5f
            else -> 0f
        }
        val totalQuizTopics = trainingRepository.getQuizTopics().size.coerceAtLeast(1)
        val quizRatio = (quizzes.size.toFloat() / totalQuizTopics).coerceAtMost(1f)

        val overall = ((kitRatio * 0.40f + checklistRatio * 0.25f + familyRatio * 0.20f + quizRatio * 0.15f) * 100).toInt()
            .coerceIn(0, 100)

        DashboardMetrics(
            preparednessPercent = overall,
            kitPreparedCount = kitItems.count { it.isPrepared },
            kitTotalCount = kitItems.size,
            familyCount = familyMembers.size,
            hasFamilyPlan = hasPlan,
            quizzesCompletedCount = quizzes.size
        )
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        baseStatsFlow,
        preferencesRepository.userSettings,
        networkMonitor.isOnline,
        sosRepository.observeQueuedCount()
    ) { metrics, settings, online, queuedSos ->
        DashboardUiState(
            userName = settings.userName,
            userCity = settings.userCity,
            isOnline = online,
            preparednessPercent = metrics.preparednessPercent,
            kitPreparedCount = metrics.kitPreparedCount,
            kitTotalCount = metrics.kitTotalCount,
            familyCount = metrics.familyCount,
            hasFamilyPlan = metrics.hasFamilyPlan,
            quizzesCompletedCount = metrics.quizzesCompletedCount,
            queuedSosCount = queuedSos,
            lastOfflineSyncMillis = settings.lastOfflineSyncMillis,
            emergencyNumbers = crisisRepository.getEmergencyNumbers()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState(
            emergencyNumbers = crisisRepository.getEmergencyNumbers()
        )
    )

    private data class DashboardMetrics(
        val preparednessPercent: Int,
        val kitPreparedCount: Int,
        val kitTotalCount: Int,
        val familyCount: Int,
        val hasFamilyPlan: Boolean,
        val quizzesCompletedCount: Int
    )
}

class CrisisGuideViewModel(
    private val crisisRepository: CrisisRepository
) : ViewModel() {

    val crisisTypes: List<CrisisType> = crisisRepository.getAllCrisisTypes()

    val allChecklists: StateFlow<List<CrisisChecklistEntity>> =
        crisisRepository.observeAllChecklists()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getGuide(type: CrisisType): CrisisGuide? = crisisRepository.getGuide(type)

    fun observeChecklist(type: CrisisType): Flow<List<CrisisChecklistEntity>> =
        crisisRepository.observeChecklist(type)

    fun toggleChecklist(id: Int, checked: Boolean) {
        viewModelScope.launch {
            crisisRepository.toggleChecklistItem(id, checked)
        }
    }
}
