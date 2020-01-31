package com.mohamedmenasy.falldetection

import java.util.*

/**
 * [FallDetectionEvent] hold data and use it to any user of the library
 */
data class FallDetectionEvent(
    val date: Date, val duration: Long
)