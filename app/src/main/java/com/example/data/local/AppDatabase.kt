package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
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

class RoomConverters {
    @TypeConverter fun fromKitCategory(value: KitCategory): String = value.name
    @TypeConverter fun toKitCategory(value: String): KitCategory = enumValueOf(value)

    @TypeConverter fun fromSafetyStatus(value: FamilySafetyStatus): String = value.name
    @TypeConverter fun toSafetyStatus(value: String): FamilySafetyStatus = enumValueOf(value)

    @TypeConverter fun fromSosStatus(value: SosStatus): String = value.name
    @TypeConverter fun toSosStatus(value: String): SosStatus = enumValueOf(value)

    @TypeConverter fun fromReportCategory(value: UrbanReportCategory): String = value.name
    @TypeConverter fun toReportCategory(value: String): UrbanReportCategory = enumValueOf(value)

    @TypeConverter fun fromReportStatus(value: UrbanReportStatus): String = value.name
    @TypeConverter fun toReportStatus(value: String): UrbanReportStatus = enumValueOf(value)

    @TypeConverter fun fromShelterType(value: ShelterType): String = value.name
    @TypeConverter fun toShelterType(value: String): ShelterType = enumValueOf(value)
}

@Database(
    entities = [
        CrisisChecklistEntity::class,
        EmergencyKitItemEntity::class,
        FamilyMemberEntity::class,
        FamilyPlanEntity::class,
        SosRequestEntity::class,
        UrbanReportEntity::class,
        ShelterPointEntity::class,
        QuizProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun crisisChecklistDao(): CrisisChecklistDao
    abstract fun emergencyKitDao(): EmergencyKitDao
    abstract fun familyDao(): FamilyDao
    abstract fun sosRequestDao(): SosRequestDao
    abstract fun urbanReportDao(): UrbanReportDao
    abstract fun shelterDao(): ShelterDao
    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hamyar_bohran.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
