package com.example.data.repository

import com.example.data.database.EyeTestDao
import com.example.data.model.EyeTestRecord
import kotlinx.coroutines.flow.Flow

class EyeTestRepository(private val eyeTestDao: EyeTestDao) {
    val allRecords: Flow<List<EyeTestRecord>> = eyeTestDao.getAllRecords()

    fun getRecordsByType(testType: String): Flow<List<EyeTestRecord>> =
        eyeTestDao.getRecordsByType(testType)

    fun getRecordsByEye(eye: String): Flow<List<EyeTestRecord>> =
        eyeTestDao.getRecordsByEye(eye)

    suspend fun insertRecord(record: EyeTestRecord): Long =
        eyeTestDao.insertRecord(record)

    suspend fun deleteRecord(id: Long) =
        eyeTestDao.deleteRecordById(id)

    suspend fun clearAll() =
        eyeTestDao.clearAllRecords()
}
