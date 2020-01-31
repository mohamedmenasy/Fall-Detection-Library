package com.mohamedmenasy.falldetection.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Dao class contains the common operations on detection data
 */
@Dao
interface FallDetectionDao {
    @Query("SELECT * FROM FallDetectionData")
    suspend fun getAll(): List<FallDetectionData>

    @Insert
    suspend fun insert(vararg fallDetectionData: FallDetectionData)

    @Delete
    suspend fun delete(fallDetectionData: FallDetectionData)
}