package com.mohamedmenasy.falldetection

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mohamedmenasy.falldetection.model.local.DetectionsDatabase
import com.mohamedmenasy.falldetection.model.local.FallDetectionDao
import com.mohamedmenasy.falldetection.model.local.FallDetectionData
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

/**
 * Test cased to test the local storage of [FallDetectionData]
 */
@RunWith(AndroidJUnit4::class)
class FallDetectionDaoTest {
    companion object {
        val testFallDetectionData = FallDetectionData(Date(), 588)
    }

    private lateinit var fallDetectionDao: FallDetectionDao
    private lateinit var db: DetectionsDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, DetectionsDatabase::class.java
        ).build()
        fallDetectionDao = db.fallDetectionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * Test insertion and retrieval of [FallDetectionData]
     */
    @Test
    fun testInsertAndGetFallDetectionData() {
        runBlocking {
            fallDetectionDao.insert(testFallDetectionData)
            val fallDetectionsFromDb = fallDetectionDao.getAll()
            assertEquals(listOf(testFallDetectionData), fallDetectionsFromDb)
        }
    }

    /**
     * Test deletion of [FallDetectionData]
     */
    @Test
    fun testDeleteFallDetectionData() {
        runBlocking {
            testFallDetectionData.id = 1
            fallDetectionDao.insert(testFallDetectionData)
            fallDetectionDao.delete(testFallDetectionData)
            val fallDetectionsFromDb = fallDetectionDao.getAll()
            assertEquals(emptyList<FallDetectionData>(), fallDetectionsFromDb)
        }
    }

}