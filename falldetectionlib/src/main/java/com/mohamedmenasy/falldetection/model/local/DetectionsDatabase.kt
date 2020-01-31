package com.mohamedmenasy.falldetection.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * [RoomDatabase] class
 */
@Database(entities = [FallDetectionData::class], version = 1)
@TypeConverters(Converters::class)
abstract class DetectionsDatabase : RoomDatabase() {
    abstract fun fallDetectionDao(): FallDetectionDao
}