package com.mohamedmenasy.falldetection.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * [Entity] class for Room database
 */
@Entity
data class FallDetectionData(
    val date: Date, val duration: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}