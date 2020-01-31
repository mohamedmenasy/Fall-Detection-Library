package com.mohamedmenasy.falldetection

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.mohamedmenasy.falldetection.FallDetection.Companion.INTENSITY_INTENT
import com.mohamedmenasy.falldetection.model.local.FallDetectionData
import com.mohamedmenasy.falldetectionlib.R
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A service which responsible for detection falls
 */
class DetectionService : Service(), SensorEventListener {

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val NOTIFICATION_ID = 999
        const val EXTRA_NOTIFICATION_ID = "android.intent.extra.NOTIFICATION_ID"
        const val ACTION_CLOSE = "CLOSE"
        const val GRAVITY_CONSTANT = 9.80665
    }

    //callback used to pass fall data to FallDetection class
    private var callbacks: Callbacks? = null

    //binder to be used to communicate with the service
    private val mBinder: IBinder = DetectionBinder()

    //sensor and detection variables
    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private var accelerationGravity = 0.0
    private var landingFlag = false
    private lateinit var intensity: FallDetection.Intensity

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    /**
     * Register the callback
     * @param source The implementation of [Callbacks]
     */
    fun registerClient(source: Any) {
        callbacks = source as Callbacks
    }

    /**
     * Initialize sensors listeners
     */
    public fun initListeners() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
            this,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    /**
     * Display notification once a fall detected
     * @param detectionDuration The fall duration im milli seconds
     */
    private fun showDetectionNotification(detectionDuration: Long) {
        val timeInSeconds = detectionDuration / 1000.0
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.fall_detectet))
            .setContentText(
                String.format(
                    getString(R.string.fall_detect_content),
                    timeInSeconds.toString()
                )
            )
            .setSmallIcon(R.drawable.ic_falling)
            .build()
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initListeners()
        intensity = intent?.extras?.get(INTENSITY_INTENT) as FallDetection.Intensity

        val notification = buildNotification()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    /**
     * Display a sticky notification for the service
     * @return [Notification] The notification
     */
    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }

        val contentIntent = PendingIntent.getActivity(
            this,
            0, Intent(), 0
        )
        val closeIntent = Intent(this, StopServiceReceiver::class.java).apply {
            action = ACTION_CLOSE
            putExtra(EXTRA_NOTIFICATION_ID, 0)
        }
        val closePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, closeIntent, 0)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.service_runnng))
            .setSmallIcon(R.drawable.ic_falling)
            .setContentIntent(contentIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel, getString(R.string.close),
                closePendingIntent
            )
            .build()

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("onAccuracyChanged", "accuracy : $accuracy")
    }

    /**
     * Callback method triggered for each change on accelerometer sensor
     * @param sensorEvent The new sensor event
     */
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            detectFall(
                sensorEvent.values[0],
                sensorEvent.values[1],
                sensorEvent.values[2]
            )
        }
    }

    /**
     * Calculate the acceleration gravity from accelerometer sensor data
     * @param x The x value from accelerometer data
     * @param y The y value from accelerometer data
     * @param z The z value from accelerometer data
     */
    private fun detectFall(
        x: Float,
        y: Float,
        z: Float
    ) {
        val acceleration = sqrt(
            x.toDouble().pow(2.0) + y.toDouble().pow(2.0) + z.toDouble().pow(2.0)
        )
        accelerationGravity = acceleration / GRAVITY_CONSTANT
        if (accelerationGravity < 0.3) {
            val startTime = System.currentTimeMillis()
            val timerHandler = Handler()

            val runnable = object : Runnable {
                override fun run() {
                    if (isFallingEventEnds(startTime, accelerationGravity)) {
                        return
                    }
                    timerHandler.postDelayed(this, 500)
                }
            }
            timerHandler.postDelayed(runnable, 0)

        }
    }

    /**
     * Determine if the falling event is ends or not
     * @param startTime The start time of the falling
     * @param accelerationGravity The current acceleration gravity
     * @return [Boolean] Is falling event ends or not
     */
    public fun isFallingEventEnds(startTime: Long, accelerationGravity: Double): Boolean {
        val millis = System.currentTimeMillis() - startTime
        var seconds = (millis / 1000).toInt()
        seconds %= 60

        if (seconds <= 1 && accelerationGravity > intensity.landingGravity) {
            landingFlag = true
        }
        if (seconds <= 3
            && landingFlag
            && accelerationGravity >= intensity.minNotMovingGravity
            && accelerationGravity <= intensity.maxNotMovingGravity
        ) {
            callbacks?.fallDetected(FallDetectionData(Date(), millis))
            showDetectionNotification(millis)
            return true
        }

        return false

    }

    /**
     * [Binder] class to communicate with the service
     */
    inner class DetectionBinder : Binder() {
        fun getService(): DetectionService {
            return this@DetectionService
        }
    }

    /**
     * [BroadcastReceiver] class to stop the service form notification
     */
    class StopServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val intent = Intent(context, DetectionService::class.java)
            context?.stopService(intent)
        }
    }

    /**
     * Interface used to pass fall events to [FallDetection]
     */
    interface Callbacks {
        fun fallDetected(event: FallDetectionData)
    }
}
