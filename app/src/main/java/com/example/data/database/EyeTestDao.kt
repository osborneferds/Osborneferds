package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.EyeTestRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface EyeTestDao {
    @Query("SELECT * FROM eye_test_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<EyeTestRecord>>

    @Query("SELECT * FROM eye_test_records WHERE testType = :testType ORDER BY timestamp DESC")
    fun getRecordsByType(testType: String): Flow<List<EyeTestRecord>>

    @Query("SELECT * FROM eye_test_records WHERE eyeTested = :eye ORDER BY timestamp DESC")
    fun getRecordsByEye(eye: String): Flow<List<EyeTestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: EyeTestRecord): Long

    @Query("DELETE FROM eye_test_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM eye_test_records")
    suspend fun clearAllRecords()
}
