package com.example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.MavaSettingsEntity
import com.example.data.local.entity.MediaDownloadStatus
import com.example.data.model.CrisisType
import com.example.data.model.ScienceCategory
import com.example.data.seed.CrisisContentSeed
import com.example.data.seed.MavaContentSeed
import com.example.data.seed.ScienceAndLabSeed
import com.example.data.system.EmergencyLocationSnapshot
import com.example.data.system.SosLocationStatus
import com.example.data.system.SosLocationTracker
import com.example.ui.viewmodel.EmergencyRecipient
import com.example.ui.viewmodel.SosUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HamyarUpdateRobolectricTest {

    @Test
    fun `first aid lessons cover all 7 required topics without fake video links`() {
        val lessons = CrisisContentSeed.trainingLessons
        assertEquals(7, lessons.size)
        val offlineMedia = CrisisContentSeed.defaultOfflineMedia()
        assertEquals(7, offlineMedia.size)
        offlineMedia.forEach { asset ->
            assertNull(asset.streamUrl)
            assertEquals(MediaDownloadStatus.NOT_LINKED, asset.status)
        }
    }

    @Test
    fun `science encyclopedia, why questions, virtual lab, and scenarios cover all required items`() {
        assertEquals(12, ScienceAndLabSeed.scienceTopics.size)
        assertTrue(ScienceCategory.entries.all { cat ->
            ScienceAndLabSeed.scienceTopics.any { it.category == cat }
        })
        assertEquals(7, ScienceAndLabSeed.whyQuestions.size)
        assertEquals(6, ScienceAndLabSeed.virtualLabExperiments.size)
        assertEquals(6, ScienceAndLabSeed.scenarios.size)
    }

    @Test
    fun `mava content and defaults provide safe toddler experience`() {
        val defaults = MavaSettingsEntity()
        assertEquals("1_3", defaults.ageGroup)
        assertTrue(defaults.soundEnabled)
        assertTrue(defaults.gamesEnabled)
        assertTrue(defaults.storiesEnabled)
        assertTrue(defaults.calmEnabled)
        assertTrue(defaults.emotionsEnabled)
        assertTrue(defaults.cartoonsEnabled)
        assertTrue(defaults.discoveryEnabled)
        assertTrue(MavaContentSeed.stories.size >= 3)
        assertTrue(MavaContentSeed.cartoons.size >= 5)
        val bundledCartoons = MavaContentSeed.cartoons.filter { it.bundledVideoResId != null }
        assertEquals(3, bundledCartoons.size)
        val cartoonMedia = MavaContentSeed.defaultMavaOfflineMedia()
        assertEquals(MavaContentSeed.cartoons.size, cartoonMedia.size)
        assertEquals(3, cartoonMedia.count { it.status == MediaDownloadStatus.DOWNLOADED })
        assertTrue(MavaContentSeed.discoveryItems.size >= 6)
    }

    @Test
    fun `room migration 1 to 2 preserves user family and sos tables and removes map and report tables`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbFile = context.getDatabasePath("test_migration_1_2.db")
        if (dbFile.exists()) dbFile.delete()

        val rawDb = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        rawDb.execSQL(
            "CREATE TABLE family_members (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, relation TEXT NOT NULL, phone TEXT NOT NULL, bloodType TEXT NOT NULL, medicalNotes TEXT NOT NULL, safetyStatus TEXT NOT NULL, lastUpdatedMillis INTEGER NOT NULL)"
        )
        rawDb.execSQL(
            "INSERT INTO family_members (name, relation, phone, bloodType, medicalNotes, safetyStatus, lastUpdatedMillis) VALUES ('مریم', 'مادر', '09120000000', 'A+', 'ندارد', 'SAFE', 1000)"
        )
        rawDb.execSQL("CREATE TABLE shelter_points (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL)")
        rawDb.execSQL("CREATE TABLE urban_reports (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, category TEXT NOT NULL)")
        rawDb.version = 1
        rawDb.close()

        val helper = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(dbFile.absolutePath)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: SupportSQLiteDatabase) {}
                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                        AppDatabase.MIGRATION_1_2.migrate(db)
                    }
                })
                .build()
        )
        val migratedDb = helper.writableDatabase
        // Family data survived
        val cursor = migratedDb.query("SELECT name, phone FROM family_members")
        assertTrue(cursor.moveToFirst())
        assertEquals("مریم", cursor.getString(0))
        assertEquals("09120000000", cursor.getString(1))
        cursor.close()

        // Dropped tables do not exist
        val tablesCursor = migratedDb.query("SELECT name FROM sqlite_master WHERE type='table'")
        val tableNames = mutableSetOf<String>()
        while (tablesCursor.moveToNext()) {
            tableNames.add(tablesCursor.getString(0))
        }
        tablesCursor.close()
        assertFalse(tableNames.contains("shelter_points"))
        assertFalse(tableNames.contains("urban_reports"))
        assertTrue(tableNames.contains("offline_media_assets"))
        assertTrue(tableNames.contains("mava_settings"))

        helper.close()
        dbFile.delete()
    }

    @Test
    fun `manifest declares foreground location only and no background location`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val packageInfo = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        )
        val permissions = packageInfo.requestedPermissions?.toSet() ?: emptySet()
        assertTrue(permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION))
        assertTrue(permissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
        assertFalse(permissions.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
    }

    @Test
    fun `sos location tracker starts idle and handles denied permission without crashing`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tracker = SosLocationTracker(context)
        assertFalse(tracker.isTracking.value)
        assertEquals(SosLocationStatus.IDLE, tracker.status.value)

        // Without granted runtime permission, startTracking fails safely to PERMISSION_DENIED
        tracker.startTracking()
        assertFalse(tracker.isTracking.value)
        assertEquals(SosLocationStatus.PERMISSION_DENIED, tracker.status.value)

        tracker.stopTracking()
        assertFalse(tracker.isTracking.value)
        assertEquals(SosLocationStatus.IDLE, tracker.status.value)
    }

    @Test
    fun `sos ui state includes crisis type, address note, live gps coords, and selected contacts`() {
        val uiState = SosUiState(
            selectedCrisisType = CrisisType.FLOOD,
            selectedEmergencyType = "درخواست امداد فوری",
            addressNote = "خیابان آزادی، پلاک ۱۲",
            locationConsentGiven = true,
            liveLocation = EmergencyLocationSnapshot(latitude = 35.69971, longitude = 51.33806),
            availableContacts = listOf(
                EmergencyRecipient("member_1", "علی", "برادر", "09121112233"),
                EmergencyRecipient("member_2", "سارا", "مادر", "09124445566")
            ),
            selectedContactIds = setOf("member_2")
        )
        assertEquals("09124445566", uiState.buildSelectedContactPhones())
        val sms = uiState.buildPreparedSmsText()
        assertTrue(sms.contains("سیل"))
        assertTrue(sms.contains("خیابان آزادی، پلاک ۱۲"))
        assertTrue(sms.contains("35.69971, 51.33806"))
        assertTrue(sms.contains("https://maps.google.com/?q=35.69971,51.33806"))
    }
}
