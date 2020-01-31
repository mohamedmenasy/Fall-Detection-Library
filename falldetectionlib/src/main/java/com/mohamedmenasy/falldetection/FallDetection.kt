package com.mohamedmenasy.falldetection

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.mohamedmenasy.falldetection.model.local.DetectionsDatabase
import com.mohamedmenasy.falldetection.model.local.FallDetectionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main class of the Detection Library
 * @param context The context used to start/stop [DetectionService].
 * @param intensity Set the detection intensity.
 */
open class FallDetection private constructor(
    private val context: Context?,
    private val intensity: Intensity?
) :
    ServiceConnection, DetectionService.Callbacks {
    companion object {
        const val INTENSITY_INTENT = "INTENSITY"
    }

    /**
     * enum class to determine the detection intensity
     *   @param landingGravity The gravity value for falling.
     *   @param minNotMovingGravity The minimum gravity value for still status.
     *   @param maxNotMovingGravity The maximum gravity value for still status.
     */
    enum class Intensity(
        val landingGravity: Float,
        val minNotMovingGravity: Float,
        val maxNotMovingGravity: Float
    ) {
        LOW(0.8f, 0.8f, 1.0f),
        MEDIUM(1f, 0.9f, 1.1f),
        HIGH(2.0f, 1.0f, 1.2f)
    }

    private lateinit var detectionService: DetectionService
    private var database: DetectionsDatabase? = context?.let {
        Room.databaseBuilder(
            it,
            DetectionsDatabase::class.java, "fall-detections"
        ).build()
    }

    override fun onServiceDisconnected(name: ComponentName?) {

    }

    /**
     * get object of [DetectionService] to register callbacks
     */
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        detectionService = (service as DetectionService.DetectionBinder).getService()
        detectionService.registerClient(this)
    }

    /**
     * callback method triggered by [DetectionService]
     * @param event The [FallDetectionData] contains the fall data.
     */
    override fun fallDetected(event: FallDetectionData) {
        storeFallEvent(event)
    }

    /**
     * start the [DetectionService]
     */
    fun startService() {
        val intent = Intent(context, DetectionService::class.java)
        intent.putExtra(INTENSITY_INTENT, intensity)
        context?.let { ContextCompat.startForegroundService(it, intent) }
        context?.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    /**
     * store detected fall event to the database
     * @param event The [FallDetectionData] contains the fall data to be saved.
     */
    @WorkerThread
    private fun storeFallEvent(event: FallDetectionData) {
        CoroutineScope(Dispatchers.IO).launch {
            database?.fallDetectionDao()?.insert(event)

        }
    }

    /**
     * [suspend] method to retrieve all fall event
     * @return A [List] of [FallDetectionEvent]s.
     */
    @WorkerThread
    suspend fun getAllEvents(): List<FallDetectionEvent>? {

        return database?.fallDetectionDao()?.getAll()
            ?.map { FallDetectionEvent(it.date, it.duration) }
    }

    /**
     * stop the [DetectionService]
     */
    fun stopService() {
        val intent = Intent(context, DetectionService::class.java)
        context?.stopService(intent)
        context?.unbindService(this)
    }

    /**
     * unbind the service
     */
    fun unbind() {
        context?.unbindService(this)
    }

    /**
     * Builder class to build the [FallDetection]
     * @param context The context used to start/stop [DetectionService].
     * @param intensity Set the detection intensity.
     */
    data class Builder(var context: Context? = null, var intensity: Intensity? = null) {

        /**
         * Set the context
         * @param context Set the context to be used for start/stop [DetectionService].
         */
        fun withContext(context: Context): Builder {
            this.context = context
            return this
        }

        /**
         * Set the intensity
         * @param intensity Set the detection intensity.
         */
        fun intensity(intensity: Intensity): Builder {
            this.intensity = intensity
            return this
        }

        /**
         * Build [FallDetection]
         * @return A full instantiated [FallDetection].
         */
        fun build() = FallDetection(context, intensity)
    }
}