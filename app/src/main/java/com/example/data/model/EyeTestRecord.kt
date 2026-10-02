package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eye_test_records")
data class EyeTestRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val testType: String,
    val eyeTested: String,
    val visualAcuity: String,
    val metricAcuity: String = "",
    val logMar: Double = 0.0,
    val scorePercent: Int = 100,
    val assessment: String = "",
    val notes: String = ""
)
