package com.example.timersleep

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import com.example.timersleep.databinding.ActivityMainBinding
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var circularProgressBar1: CircularProgressBar
    private lateinit var circularProgressBar2: CircularProgressBar

    private var gestureDetector: GestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        circularProgressBar1 = binding.circularProgressBar1
        circularProgressBar2 = binding.circularProgressBar2
        gestureDetector = GestureDetector(this,CircularProgressBarGestureListener())
        circularProgressBar1.progress = 0f
        circularProgressBar2.progress = 0f
        simulateProgress()

    }

    private fun simulateProgress() {
        val totalProgressTime = 10
        val handler = Handler(Looper.getMainLooper())

        Thread {
            for (progress in 0 until totalProgressTime step 10) {
                handler.post {
                    circularProgressBar1.progress = progress.toFloat()
                    circularProgressBar2.progress = progress.toFloat()
                }
                try {
                    Thread.sleep(10)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector?.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    inner class CircularProgressBarGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            // Xử lý sự kiện kéo
            if (e1 != null) {
                handleProgressBarScroll(circularProgressBar1, e2)
                handleProgressBarScroll(circularProgressBar2, e2)
            }
            return true
        }
        private fun handleProgressBarScroll(
            circularProgressBar: CircularProgressBar,
            e2: MotionEvent
        ) {
            val centerX = circularProgressBar.x + circularProgressBar.width / 2
            val centerY = circularProgressBar.y + circularProgressBar.height / 2

            val distanceToTouch = sqrt((e2.x - centerX).toDouble().pow(2.0)
                    + (e2.y - centerY).toDouble().pow(2.0)).toFloat()

            // Tính toán góc giữa tâm và điểm chạm tay
            val angle = atan2((e2.y - centerY).toDouble(), (e2.x - centerX).toDouble())
            val progressBarRadius = circularProgressBar.width / 2

            val distanceToEdge = progressBarRadius - circularProgressBar.progressBarWidth / 2
            val distanceToEdgeTouch = distanceToEdge - circularProgressBar.progressBarWidth / 2

            if (distanceToTouch in distanceToEdgeTouch..distanceToEdge) {
                // Ánh xạ góc thành giá trị tiến trình
                val progress = ((angle / (2 * Math.PI) + 0.5) * 100).toFloat()

                circularProgressBar.animate().duration = 0
                circularProgressBar.progress = progress
            }
        }
    }
}