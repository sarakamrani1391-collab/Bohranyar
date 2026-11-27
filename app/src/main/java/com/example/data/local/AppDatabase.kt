package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

class RoomConverters {
    @TypeConverter fun fromKitCategory(value: KitCategory): String = value.name
    @TypeConverter fun toKitCategory(value: String): KitCategory = enumValueOf(value)

    @TypeConverter fun fromSafetyStatus(value: FamilySafetyStatus): String = value.name
    @TypeConverter fun toSafetyStatus(value: String): FamilySafetyStatus = enumValueOf(value)

    @TypeConverter fun fromSosStatus(value: SosStatus): String = value.name
    @TypeConverter fun toSosStatus(value: String): SosStatus = enumValueOf(value)

    @TypeConverter fun fromMediaCategory(value: MediaCategory): String = value.name
    @TypeConverter fun toMediaCategory(value: String): MediaCategory = enumValueOf(value)

    @TypeConverter fun fromMediaDownloadStatus(value: MediaDownloadStatus): String = value.name
    @TypeConverter fun toMediaDownloadStatus(value: String): MediaDownloadStatus = enumValueOf(value)
}

@Database(
    entities = [
        CrisisChecklistEntity::class,
        EmergencyKitItemEntity::class,
        FamilyMemberEntity::class,
        FamilyPlanEntity::class,
        SosRequestEntity::class,
        QuizProgressEntity::class,
        OfflineMediaEntity::class,
        MavaSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun crisisChecklistDao(): CrisisChecklistDao
    abstract fun emergencyKitDao(): EmergencyKitDao
    abstract fun familyDao(): FamilyDao
    abstract fun sosRequestDao(): SosRequestDao
    abstract fun trainingDao(): TrainingDao
    abstract fun offlineMediaDao(): OfflineMediaDao
    abstract fun mavaSettingsDao(): MavaSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS shelter_points")
                db.execSQL("DROP TABLE IF EXISTS urban_reports")
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS offline_media_assets (
                        mediaId TEXT NOT NULL PRIMARY KEY,
                        category TEXT NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        sourceAttribution TEXT NOT NULL,
                        streamUrl TEXT,
                        localFilePath TEXT,
                        status TEXT NOT NULL,
                        durationLabel TEXT NOT NULL,
                        updatedAtMillis INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS mava_settings (
                        id INTEGER NOT NULL PRIMARY KEY,
                        ageGroup TEXT NOT NULL,
                        soundEnabled INTEGER NOT NULL,
                        dailyLimitMinutes INTEGER NOT NULL,
                        gamesEnabled INTEGER NOT NULL,
                        storiesEnabled INTEGER NOT NULL,
                        calmEnabled INTEGER NOT NULL,
                        emotionsEnabled INTEGER NOT NULL,
                        cartoonsEnabled INTEGER NOT NULL,
                        discoveryEnabled INTEGER NOT NULL,
                        updatedAtMillis INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hamyar_bohran.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
