package com.mohamedmenasy.falldetection.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mohamedmenasy.falldetection.FallDetection
import com.mohamedmenasy.falldetection.FallDetectionEvent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/*
TODO
readme
upload
*/

/**
 * MainActivity
 */
class MainActivity : AppCompatActivity() {
    private val detectionAdapter =
        DetectionsAdapter(emptyList<FallDetectionEvent>().toMutableList())
    private val detection: FallDetection by lazy {
        FallDetection.Builder()
            .withContext(this)
            .intensity(FallDetection.Intensity.LOW)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        detection.startService()
        initViews()

        detectionSR.setOnRefreshListener {
            loadDetectionData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        detection.unbind()
    }

    /**
     * Load date from detection library and display it into recyclerview
     */
    private fun loadDetectionData() {
        CoroutineScope(Dispatchers.IO).launch {
            val detectionData = detection.getAllEvents()?.toMutableList()
            CoroutineScope(Dispatchers.Main).launch {
                detectionSR.isRefreshing = false
                if (detectionData.isNullOrEmpty()) {
                    emptyDataTV.visibility = View.VISIBLE
                    detectionsRV.visibility = View.GONE
                    detectionSR.isRefreshing = false

                } else {
                    emptyDataTV.visibility = View.INVISIBLE
                    detectionsRV.visibility = View.VISIBLE
                    detectionAdapter.setData(detectionData)
                }
            }


        }
    }

    /**
     * Initialise main activity's views
     */
    private fun initViews() {
        detectionsRV.apply {
            adapter = detectionAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        detectionSR.isRefreshing = true
        loadDetectionData()
    }

}
