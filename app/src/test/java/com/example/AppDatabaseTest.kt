package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.database.EyeTestDao
import com.example.data.model.EyeTestRecord
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: EyeTestDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.eyeTestDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndRetrieveRecord() = runBlocking {
        val record = EyeTestRecord(
            testType = "Snellen Letters",
            eyeTested = "Right Eye (OD)",
            visualAcuity = "20/20",
            metricAcuity = "6/6",
            logMar = 0.0,
            scorePercent = 100,
            assessment = "Normal Visual Acuity",
            notes = "Screening at 20 feet"
        )
        val insertedId = dao.insertRecord(record)
        assertTrue(insertedId > 0)

        val records = dao.getAllRecords().first()
        assertEquals(1, records.size)
        val retrieved = records[0]
        assertEquals("Snellen Letters", retrieved.testType)
        assertEquals("Right Eye (OD)", retrieved.eyeTested)
        assertEquals("20/20", retrieved.visualAcuity)
        assertEquals("6/6", retrieved.metricAcuity)
    }

    @Test
    fun queryRecordsByTestType() = runBlocking {
        dao.insertRecord(
            EyeTestRecord(
                testType = "Ishihara Plates",
                eyeTested = "Both Eyes (OU)",
                visualAcuity = "Normal Trichromat",
                scorePercent = 100
            )
        )
        dao.insertRecord(
            EyeTestRecord(
                testType = "Astigmatism Dial",
                eyeTested = "Left Eye (OS)",
                visualAcuity = "No Astigmatism",
                scorePercent = 100
            )
        )

        val ishiharaRecords = dao.getRecordsByType("Ishihara Plates").first()
        assertEquals(1, ishiharaRecords.size)
        assertEquals("Ishihara Plates", ishiharaRecords[0].testType)

        val astigmatismRecords = dao.getRecordsByType("Astigmatism Dial").first()
        assertEquals(1, astigmatismRecords.size)
        assertEquals("Astigmatism Dial", astigmatismRecords[0].testType)
    }

    @Test
    fun deleteRecordByIdAndClearAll() = runBlocking {
        val id1 = dao.insertRecord(
            EyeTestRecord(
                testType = "Snellen Letters",
                eyeTested = "Right Eye (OD)",
                visualAcuity = "20/40"
            )
        )
        val id2 = dao.insertRecord(
            EyeTestRecord(
                testType = "Snellen Letters",
                eyeTested = "Left Eye (OS)",
                visualAcuity = "20/25"
            )
        )

        var records = dao.getAllRecords().first()
        assertEquals(2, records.size)

        // Delete one
        dao.deleteRecordById(id1)
        records = dao.getAllRecords().first()
        assertEquals(1, records.size)
        assertEquals(id2, records[0].id)

        // Clear all
        dao.clearAllRecords()
        records = dao.getAllRecords().first()
        assertTrue(records.isEmpty())
    }
}
