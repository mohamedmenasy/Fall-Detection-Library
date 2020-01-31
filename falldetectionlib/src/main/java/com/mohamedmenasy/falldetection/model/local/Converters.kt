package com.mohamedmenasy.falldetection.model.local

import androidx.room.TypeConverter
import java.util.*

/**
 * [TypeConverter] class to convert date class to time stamp
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}