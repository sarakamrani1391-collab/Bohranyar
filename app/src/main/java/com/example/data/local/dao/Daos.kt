package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CrisisChecklistEntity
import com.example.data.local.entity.EmergencyKitItemEntity
import com.example.data.local.entity.FamilyMemberEntity
import com.example.data.local.entity.FamilyPlanEntity
import com.example.data.local.entity.FamilySafetyStatus
import com.example.data.local.entity.QuizProgressEntity
import com.example.data.local.entity.ShelterPointEntity
import com.example.data.local.entity.SosRequestEntity
import com.example.data.local.entity.SosStatus
import com.example.data.local.entity.UrbanReportEntity
import com.example.data.local.entity.UrbanReportStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface CrisisChecklistDao {
    @Query("SELECT * FROM crisis_checklists WHERE crisisId = :crisisId ORDER BY sortOrder ASC")
    fun getChecklistForCrisis(crisisId: String): Flow<List<CrisisChecklistEntity>>

    @Query("SELECT * FROM crisis_checklists")
    fun getAllChecklists(): Flow<List<CrisisChecklistEntity>>

    @Query("SELECT COUNT(*) FROM crisis_checklists")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CrisisChecklistEntity>)

    @Query("UPDATE crisis_checklists SET isChecked = :checked WHERE id = :id")
    suspend fun setChecked(id: Int, checked: Boolean)
}

@Dao
interface EmergencyKitDao {
    @Query("SELECT * FROM emergency_kit_items ORDER BY isPrepared ASC, id ASC")
    fun getAllItems(): Flow<List<EmergencyKitItemEntity>>

    @Query("SELECT COUNT(*) FROM emergency_kit_items")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<EmergencyKitItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EmergencyKitItemEntity)

    @Query("UPDATE emergency_kit_items SET isPrepared = :prepared WHERE id = :id")
    suspend fun setPrepared(id: Int, prepared: Boolean)

    @Query("DELETE FROM emergency_kit_items WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface FamilyDao {
    @Query("SELECT * FROM family_members ORDER BY id ASC")
    fun getAllMembers(): Flow<List<FamilyMemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: FamilyMemberEntity)

    @Update
    suspend fun updateMember(member: FamilyMemberEntity)

    @Query("UPDATE family_members SET safetyStatus = :status, lastUpdatedMillis = :timestamp WHERE id = :id")
    suspend fun updateSafetyStatus(id: Int, status: FamilySafetyStatus, timestamp: Long)

    @Query("DELETE FROM family_members WHERE id = :id")
    suspend fun deleteMember(id: Int)

    @Query("SELECT * FROM family_plan WHERE id = 1")
    fun getFamilyPlan(): Flow<FamilyPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFamilyPlan(plan: FamilyPlanEntity)
}

@Dao
interface SosRequestDao {
    @Query("SELECT * FROM sos_requests ORDER BY createdAtMillis DESC")
    fun getAllRequests(): Flow<List<SosRequestEntity>>

    @Query("SELECT COUNT(*) FROM sos_requests WHERE status = :status")
    fun countByStatus(status: SosStatus): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: SosRequestEntity): Long

    @Query("UPDATE sos_requests SET status = :newStatus WHERE status = :oldStatus")
    suspend fun transitionStatus(oldStatus: SosStatus, newStatus: SosStatus)

    @Query("UPDATE sos_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: SosStatus)

    @Query("DELETE FROM sos_requests WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface UrbanReportDao {
    @Query("SELECT * FROM urban_reports ORDER BY createdAtMillis DESC")
    fun getAllReports(): Flow<List<UrbanReportEntity>>

    @Query("SELECT COUNT(*) FROM urban_reports WHERE status = :status")
    fun countByStatus(status: UrbanReportStatus): Flow<Int>

    @Query("SELECT COUNT(*) FROM urban_reports")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reports: List<UrbanReportEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: UrbanReportEntity): Long

    @Query("UPDATE urban_reports SET status = :newStatus WHERE status = :oldStatus AND isDemoData = 0")
    suspend fun syncOfflineReports(oldStatus: UrbanReportStatus, newStatus: UrbanReportStatus)

    @Query("DELETE FROM urban_reports WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Dao
interface ShelterDao {
    @Query("SELECT * FROM shelter_points ORDER BY isVerified DESC, isDemoData ASC, id ASC")
    fun getAllShelters(): Flow<List<ShelterPointEntity>>

    @Query("SELECT COUNT(*) FROM shelter_points")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(shelters: List<ShelterPointEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shelter: ShelterPointEntity)

    @Query("DELETE FROM shelter_points WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE shelter_points SET cachedAtMillis = :timestamp")
    suspend fun updateCacheTimestamp(timestamp: Long)
}

@Dao
interface TrainingDao {
    @Query("SELECT * FROM quiz_progress")
    fun getAllProgress(): Flow<List<QuizProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: QuizProgressEntity)
}
