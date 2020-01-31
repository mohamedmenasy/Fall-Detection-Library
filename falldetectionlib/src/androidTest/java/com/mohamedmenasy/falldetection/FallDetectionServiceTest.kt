package com.mohamedmenasy.falldetection

import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ServiceTestRule
import com.mohamedmenasy.falldetection.model.local.FallDetectionData
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Test cases for [DetectionService]
 */
@RunWith(AndroidJUnit4::class)
class FallDetectionServiceTest {
    companion object {
        const val testGravityData = 1.1
    }

    private lateinit var serviceIntent: Intent
    private lateinit var binder: IBinder
    private lateinit var service: DetectionService

    @get:Rule
    val serviceRule = ServiceTestRule()

    @Before
    fun setup() {
        serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            DetectionService::class.java
        ).apply {
            putExtra(FallDetection.INTENSITY_INTENT, FallDetection.Intensity.MEDIUM)
        }
        binder = serviceRule.bindService(serviceIntent)
        service = (binder as DetectionService.DetectionBinder).getService()
    }

    /**
     * Test case to test the falling event calculations using medium intensity
     */
    @Test
    fun testFallingEventWithMediumIntensity() {
        service.registerClient(object : DetectionService.Callbacks {
            override fun fallDetected(event: FallDetectionData) {
                Assert.assertNotNull(event)
            }
        })
        val intensityDeclaredField = DetectionService::class.java.getDeclaredField("intensity")
        intensityDeclaredField.isAccessible = true
        intensityDeclaredField.set(service, FallDetection.Intensity.MEDIUM)

        getInstrumentation().runOnMainSync {
            service.initListeners()
            val result = service.isFallingEventEnds(Date().time, testGravityData)

            val callbacksDeclaredField = DetectionService::class.java.getDeclaredField("callbacks")
            callbacksDeclaredField.isAccessible = true
            val callbacks = callbacksDeclaredField.get(service)
            Assert.assertNotNull(callbacks)
            Assert.assertTrue(result)
        }

    }

    /**
     * Test case to test the falling event calculations using high intensity
     */
    @Test
    fun testFallingEventWithHighIntensity() {
        val intensity = DetectionService::class.java.getDeclaredField("intensity")
        intensity.isAccessible = true
        intensity.set(service, FallDetection.Intensity.HIGH)
        getInstrumentation().runOnMainSync {
            service.initListeners()
            val result = service.isFallingEventEnds(Date().time, testGravityData)
            Assert.assertFalse(result)
        }

    }
}