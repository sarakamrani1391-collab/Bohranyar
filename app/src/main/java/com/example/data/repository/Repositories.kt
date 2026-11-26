package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.dao.CrisisChecklistDao
import com.example.data.local.dao.EmergencyKitDao
import com.example.data.local.dao.FamilyDao
import com.example.data.local.dao.ShelterDao
import com.example.data.local.dao.SosRequestDao
import com.example.data.local.dao.TrainingDao
import com.example.data.local.dao.UrbanReportDao
import com.example.data.local.entity.CrisisChecklistEntity
import com.example.data.local.entity.EmergencyKitItemEntity
import com.example.data.local.entity.FamilyMemberEntity
import com.example.data.local.entity.FamilyPlanEntity
import com.example.data.local.entity.FamilySafetyStatus
import com.example.data.local.entity.KitCategory
import com.example.data.local.entity.QuizProgressEntity
import com.example.data.local.entity.ShelterPointEntity
import com.example.data.local.entity.ShelterType
import com.example.data.local.entity.SosRequestEntity
import com.example.data.local.entity.SosStatus
import com.example.data.local.entity.UrbanReportCategory
import com.example.data.local.entity.UrbanReportEntity
import com.example.data.local.entity.UrbanReportStatus
import com.example.data.model.CrisisGuide
import com.example.data.model.CrisisType
import com.example.data.model.EmergencyContactNumber
import com.example.data.model.QuizQuestion
import com.example.data.model.QuizTopic
import com.example.data.model.SampleAdvisory
import com.example.data.model.TrainingLesson
import com.example.data.seed.CrisisContentSeed
import com.example.data.system.ImageStorageHelper
import kotlinx.coroutines.flow.Flow

class CrisisRepository(
    private val checklistDao: CrisisChecklistDao
) {
    fun getAllCrisisTypes(): List<CrisisType> = CrisisType.entries

    fun getGuide(type: CrisisType): CrisisGuide? = CrisisContentSeed.guides[type]

    fun getEmergencyNumbers(): List<EmergencyContactNumber> = CrisisContentSeed.emergencyNumbers

    fun getSampleAdvisories(): List<SampleAdvisory> = CrisisContentSeed.sampleAdvisories

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
        latitude: Double?,
        longitude: Double?,
        locationConsentGiven: Boolean,
        isOnline: Boolean
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

    suspend fun markSharedByUser(id: Int) {
        sosDao.updateStatus(id, SosStatus.SHARED_BY_USER)
    }

    suspend fun syncQueuedRequests() {
        sosDao.transitionStatus(SosStatus.QUEUED_OFFLINE, SosStatus.SENT_SYNCED)
    }

    suspend fun deleteRequest(id: Int) = sosDao.deleteById(id)
}

class ReportRepository(
    private val reportDao: UrbanReportDao,
    private val appContext: Context
) {
    fun observeAllReports(): Flow<List<UrbanReportEntity>> = reportDao.getAllReports()

    fun observeQueuedCount(): Flow<Int> = reportDao.countByStatus(UrbanReportStatus.QUEUED_OFFLINE)

    suspend fun submitReport(
        category: UrbanReportCategory,
        title: String,
        description: String,
        imageUri: Uri?,
        latitude: Double?,
        longitude: Double?,
        locationConsentGiven: Boolean,
        isOnline: Boolean
    ) {
        val savedImagePath = imageUri?.let { ImageStorageHelper.copyImageToInternalStorage(appContext, it) }
        val status = if (isOnline) {
            UrbanReportStatus.SUBMITTED_UNVERIFIED
        } else {
            UrbanReportStatus.QUEUED_OFFLINE
        }
        reportDao.insert(
            UrbanReportEntity(
                category = category,
                title = title.trim(),
                description = description.trim(),
                imagePath = savedImagePath,
                latitude = latitude,
                longitude = longitude,
                locationConsentGiven = locationConsentGiven,
                status = status,
                isDemoData = false,
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun syncOfflineReports() {
        reportDao.syncOfflineReports(
            oldStatus = UrbanReportStatus.QUEUED_OFFLINE,
            newStatus = UrbanReportStatus.SUBMITTED_UNVERIFIED
        )
    }

    suspend fun deleteReport(id: Int) = reportDao.deleteById(id)

    suspend fun seedIfNeeded() {
        if (reportDao.getCount() == 0) {
            reportDao.insertAll(CrisisContentSeed.defaultSampleReports(System.currentTimeMillis()))
        }
    }
}

class MapRepository(
    private val shelterDao: ShelterDao
) {
    fun observeShelters(): Flow<List<ShelterPointEntity>> = shelterDao.getAllShelters()

    suspend fun addPersonalSafePoint(
        name: String,
        address: String,
        latitude: Double,
        longitude: Double,
        note: String
    ) {
        shelterDao.insert(
            ShelterPointEntity(
                name = name.trim(),
                type = ShelterType.FAMILY_POINT,
                address = address.trim(),
                latitude = latitude,
                longitude = longitude,
                capacityNote = note.trim(),
                phone = "",
                isVerified = true,
                isDemoData = false,
                cachedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteShelter(id: Int) = shelterDao.deleteById(id)

    suspend fun refreshOfflineCacheTimestamp(timestamp: Long = System.currentTimeMillis()) {
        shelterDao.updateCacheTimestamp(timestamp)
    }

    suspend fun seedIfNeeded() {
        if (shelterDao.getCount() == 0) {
            shelterDao.insertAll(CrisisContentSeed.defaultSampleShelters(System.currentTimeMillis()))
        }
    }
}

class TrainingRepository(
    private val trainingDao: TrainingDao
) {
    fun getLessons(): List<TrainingLesson> = CrisisContentSeed.trainingLessons

    fun getQuizTopics(): List<QuizTopic> = CrisisContentSeed.quizTopics

    fun getQuestionsForTopic(topicId: String): List<QuizQuestion> =
        CrisisContentSeed.quizQuestions.filter { it.topicId == topicId }

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
