package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.repository.CrisisRepository
import com.example.data.repository.FamilyRepository
import com.example.data.repository.KitRepository
import com.example.data.repository.MavaRepository
import com.example.data.repository.OfflineMediaRepository
import com.example.data.repository.ScienceAndLabRepository
import com.example.data.repository.SosRepository
import com.example.data.repository.TrainingRepository
import com.example.data.system.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppContainer(application: Application) {
    private val database = AppDatabase.getInstance(application)

    val preferencesRepository = UserPreferencesRepository(application)
    val networkMonitor = NetworkMonitor(application)

    val crisisRepository = CrisisRepository(database.crisisChecklistDao())
    val kitRepository = KitRepository(database.emergencyKitDao())
    val familyRepository = FamilyRepository(database.familyDao())
    val sosRepository = SosRepository(database.sosRequestDao())
    val trainingRepository = TrainingRepository(database.trainingDao())
    val scienceAndLabRepository = ScienceAndLabRepository()
    val offlineMediaRepository = OfflineMediaRepository(database.offlineMediaDao(), application)
    val mavaRepository = MavaRepository(database.mavaSettingsDao())
}

class CrisisApplication : Application() {
    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        appScope.launch {
            container.crisisRepository.seedIfNeeded()
            container.kitRepository.seedIfNeeded()
            container.offlineMediaRepository.seedIfNeeded()
            container.preferencesRepository.updateOfflineSyncTimestamp()
        }

        // Automatically transition queued offline SOS requests when connectivity is restored
        appScope.launch {
            container.networkMonitor.isOnline.collectLatest { online ->
                if (online) {
                    container.sosRepository.syncQueuedRequests()
                    container.preferencesRepository.updateOfflineSyncTimestamp()
                }
            }
        }
    }
}
