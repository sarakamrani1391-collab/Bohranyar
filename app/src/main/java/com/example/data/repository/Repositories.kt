package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.dao.CrisisChecklistDao
import com.example.data.local.dao.EmergencyKitDao
import com.example.data.local.dao.FamilyDao
import com.example.data.local.dao.MavaSettingsDao
import com.example.data.local.dao.OfflineMediaDao
import com.example.data.local.dao.SosRequestDao
import com.example.data.local.dao.TrainingDao
import com.example.data.local.entity.CrisisChecklistEntity
import com.example.data.local.entity.EmergencyKitItemEntity
import com.example.data.local.entity.FamilyMemberEntity
import com.example.data.local.entity.FamilyPlanEntity
import com.example.data.local.entity.FamilySafetyStatus
import com.example.data.local.entity.KitCategory
import com.example.data.local.entity.MavaSettingsEntity
import com.example.data.local.entity.MediaCategory
import com.example.data.local.entity.MediaDownloadStatus
import com.example.data.local.entity.OfflineMediaEntity
import com.example.data.local.entity.QuizProgressEntity
import com.example.data.local.entity.SosRequestEntity
import com.example.data.local.entity.SosStatus
import com.example.data.model.CrisisGuide
import com.example.data.model.CrisisType
import com.example.data.model.EmergencyContactNumber
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizTopic
import com.example.data.model.ScenarioItem
import com.example.data.model.ScienceCategory
import com.example.data.model.ScienceTopic
import com.example.data.model.TrainingLesson
import com.example.data.model.VirtualLabExperiment
import com.example.data.model.WhyQuestion
import com.example.data.seed.CrisisContentSeed
import com.example.data.seed.MavaContentSeed
import com.example.data.seed.ScienceAndLabSeed
import com.example.data.system.MediaFileStorageHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CrisisRepository(
    private val checklistDao: CrisisChecklistDao
) {
    fun getAllCrisisTypes(): List<CrisisType> = CrisisType.entries

    fun getGuide(type: CrisisType): CrisisGuide? = CrisisContentSeed.guides[type]

    fun getEmergencyNumbers(): List<EmergencyContactNumber> = CrisisContentSeed.emergencyNumbers

    fun observeChecklist(crisisType: CrisisType): Flow<List<CrisisChecklistEntity>> =
        checklistDao.getChecklistForCrisis(crisisType.id)

    fun observeAllChecklists(): Flow<List<CrisisChecklistEntity>> =
        checklistDao.getAllChecklists()

    suspend fun toggleChecklistItem(id: Int, isChecked: Boolean) {
        checklistDao.setChecked(id, isChecked)
    }

    suspend fun seedIfNeeded() {
        if (checklistDao.getCount() == 0) {
            checklistDao.insertAll(CrisisContentSeed.defaultCrisisChecklists())
        }
    }
}

class KitRepository(
    private val kitDao: EmergencyKitDao
) {
    fun observeAllItems(): Flow<List<EmergencyKitItemEntity>> = kitDao.getAllItems()

    suspend fun togglePrepared(id: Int, prepared: Boolean) = kitDao.setPrepared(id, prepared)

    suspend fun addCustomItem(name: String, category: KitCategory, quantity: String) {
        kitDao.insert(
            EmergencyKitItemEntity(
                name = name.trim(),
                category = category,
                quantity = quantity.ifBlank { "۱ عدد" },
                isPrepared = false,
                isCustom = true
            )
        )
    }

    suspend fun deleteItem(id: Int) = kitDao.deleteById(id)

    suspend fun seedIfNeeded() {
        if (kitDao.getCount() == 0) {
            kitDao.insertAll(CrisisContentSeed.defaultKitItems())
        }
    }
}

class FamilyRepository(
    private val familyDao: FamilyDao
) {
    fun observeMembers(): Flow<List<FamilyMemberEntity>> = familyDao.getAllMembers()

    fun observePlan(): Flow<FamilyPlanEntity?> = familyDao.getFamilyPlan()

    suspend fun addMember(
        name: String,
        relation: String,
        phone: String,
        bloodType: String,
        medicalNotes: String
    ) {
        familyDao.insertMember(
            FamilyMemberEntity(
                name = name.trim(),
                relation = relation.trim(),
                phone = phone.trim(),
                bloodType = bloodType.trim(),
                medicalNotes = medicalNotes.trim(),
                safetyStatus = FamilySafetyStatus.UNKNOWN,
                lastUpdatedMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateSafetyStatus(id: Int, status: FamilySafetyStatus) {
        familyDao.updateSafetyStatus(id, status, System.currentTimeMillis())
    }

    suspend fun deleteMember(id: Int) = familyDao.deleteMember(id)

    suspend fun savePlan(
        primaryPoint: String,
        secondaryPoint: String,
        outOfCityName: String,
        outOfCityPhone: String,
        notes: String
    ) {
        familyDao.upsertFamilyPlan(
            FamilyPlanEntity(
                id = 1,
                primaryMeetingPoint = primaryPoint.trim(),
                secondaryMeetingPoint = secondaryPoint.trim(),
                outOfCityContactName = outOfCityName.trim(),
                outOfCityContactPhone = outOfCityPhone.trim(),
                emergencyNotes = notes.trim(),
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }
}

class SosRepository(
    private val sosDao: SosRequestDao
) {
    fun observeAllRequests(): Flow<List<SosRequestEntity>> = sosDao.getAllRequests()

    fun observeQueuedCount(): Flow<Int> = sosDao.countByStatus(SosStatus.QUEUED_OFFLINE)

    suspend fun createSosRequest(
        emergencyType: String,
        message: String,
        isOnline: Boolean,
        latitude: Double? = null,
        longitude: Double? = null,
        locationConsentGiven: Boolean = false
    ): SosRequestEntity {
        val status = if (isOnline) SosStatus.SENT_SYNCED else SosStatus.QUEUED_OFFLINE
        val entity = SosRequestEntity(
            emergencyType = emergencyType,
            message = message,
            latitude = latitude,
            longitude = longitude,
            locationConsentGiven = locationConsentGiven,
            status = status,
            createdAtMillis = System.currentTimeMillis()
        )
        val newId = sosDao.insert(entity).toInt()
        return entity.copy(id = newId)
    }

    suspend fun updateSosLocationAndMessage(
        id: Int,
        latitude: Double?,
        longitude: Double?,
        locationConsentGiven: Boolean,
        updatedMessage: String
    ) {
        sosDao.updateLocationAndMessage(
            id = id,
            latitude = latitude,
            longitude = longitude,
            consent = locationConsentGiven,
            message = updatedMessage
        )
    }

    suspend fun markSharedByUser(id: Int) {
        sosDao.updateStatus(id, SosStatus.SHARED_BY_USER)
    }

    suspend fun resolveSosRequest(id: Int) {
        sosDao.updateStatus(id, SosStatus.RESOLVED)
    }

    suspend fun cancelSosRequest(id: Int) {
        sosDao.updateStatus(id, SosStatus.CANCELLED)
    }

    suspend fun syncQueuedRequests() {
        sosDao.transitionStatus(SosStatus.QUEUED_OFFLINE, SosStatus.SENT_SYNCED)
    }

    suspend fun deleteRequest(id: Int) = sosDao.deleteById(id)
}

class TrainingRepository(
    private val trainingDao: TrainingDao
) {
    fun getLessons(): List<TrainingLesson> = CrisisContentSeed.trainingLessons

    fun getLessonById(id: String): TrainingLesson? =
        CrisisContentSeed.trainingLessons.firstOrNull { it.id == id }

    fun getQuizTopics(): List<QuizTopic> = CrisisContentSeed.quizTopics

    fun getQuestionsForTopic(topicId: String): List<QuizQuestion> =
        CrisisContentSeed.quizQuestions.filter { it.topicId == topicId } +
            ScienceAndLabSeed.scienceQuizQuestions.filter { it.topicId == topicId }

    fun observeQuizProgress(): Flow<List<QuizProgressEntity>> = trainingDao.getAllProgress()

    suspend fun saveQuizResult(topicId: String, score: Int, total: Int, previousBest: Int) {
        trainingDao.upsertProgress(
            QuizProgressEntity(
                topicId = topicId,
                bestScore = maxOf(score, previousBest),
                totalQuestions = total,
                completedAtMillis = System.currentTimeMillis()
            )
        )
    }
}

class ScienceAndLabRepository {
    fun getAllTopics(): List<ScienceTopic> = ScienceAndLabSeed.scienceTopics

    fun getTopicsByCategory(category: ScienceCategory?): List<ScienceTopic> =
        if (category == null) ScienceAndLabSeed.scienceTopics
        else ScienceAndLabSeed.scienceTopics.filter { it.category == category }

    fun getTopicById(id: String): ScienceTopic? =
        ScienceAndLabSeed.scienceTopics.firstOrNull { it.id == id }

    fun getWhyQuestions(): List<WhyQuestion> = ScienceAndLabSeed.whyQuestions

    fun getVirtualLabExperiments(): List<VirtualLabExperiment> = ScienceAndLabSeed.virtualLabExperiments

    fun getScenarios(): List<ScenarioItem> = ScienceAndLabSeed.scenarios
}

class OfflineMediaRepository(
    private val mediaDao: OfflineMediaDao,
    private val appContext: Context
) {
    fun observeByCategory(category: MediaCategory): Flow<List<OfflineMediaEntity>> =
        mediaDao.getByCategory(category)

    fun observeAll(): Flow<List<OfflineMediaEntity>> = mediaDao.getAll()

    fun observeMedia(mediaId: String): Flow<OfflineMediaEntity?> = mediaDao.observeById(mediaId)

    suspend fun seedIfNeeded() {
        val allAssets = CrisisContentSeed.defaultOfflineMedia() + MavaContentSeed.defaultMavaOfflineMedia()
        mediaDao.insertMissing(allAssets)
    }

    suspend fun importLocalVideo(mediaId: String, category: MediaCategory, uri: Uri): Boolean {
        val saved = MediaFileStorageHelper.importLocalVideo(appContext, mediaId, category, uri)
        return if (saved != null) {
            mediaDao.updateLocalFile(
                mediaId = mediaId,
                localPath = saved,
                status = MediaDownloadStatus.DOWNLOADED,
                updatedAt = System.currentTimeMillis()
            )
            true
        } else {
            false
        }
    }

    suspend fun downloadFromSource(asset: OfflineMediaEntity): Boolean {
        val url = asset.streamUrl
        if (url.isNullOrBlank()) return false
        mediaDao.updateLocalFile(asset.mediaId, asset.localFilePath, MediaDownloadStatus.DOWNLOADING, System.currentTimeMillis())
        val saved = MediaFileStorageHelper.downloadRemoteVideo(appContext, asset.mediaId, asset.category, url)
        return if (saved != null) {
            mediaDao.updateLocalFile(asset.mediaId, saved, MediaDownloadStatus.DOWNLOADED, System.currentTimeMillis())
            true
        } else {
            mediaDao.updateLocalFile(asset.mediaId, null, MediaDownloadStatus.NOT_DOWNLOADED, System.currentTimeMillis())
            false
        }
    }

    suspend fun removeOfflineVideo(asset: OfflineMediaEntity) {
        MediaFileStorageHelper.deleteLocalVideo(asset.localFilePath)
        val fallbackStatus = if (asset.streamUrl.isNullOrBlank()) {
            MediaDownloadStatus.NOT_LINKED
        } else {
            MediaDownloadStatus.NOT_DOWNLOADED
        }
        mediaDao.updateLocalFile(asset.mediaId, null, fallbackStatus, System.currentTimeMillis())
    }
}

class MavaRepository(
    private val settingsDao: MavaSettingsDao
) {
    fun observeSettings(): Flow<MavaSettingsEntity> =
        settingsDao.observeSettings().map { it ?: MavaSettingsEntity() }

    suspend fun saveSettings(settings: MavaSettingsEntity) {
        settingsDao.upsert(settings.copy(updatedAtMillis = System.currentTimeMillis()))
    }

    suspend fun seedIfNeeded() {
        settingsDao.upsert(MavaSettingsEntity())
    }
}
