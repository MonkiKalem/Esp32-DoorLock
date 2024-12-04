package com.example.doorlockesp32

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var timerText: TextView
    private lateinit var countDownTimer: CountDownTimer
    private val database = FirebaseDatabase.getInstance()
    private val relayRef = database.getReference("relay")
    private var isTimerRunning = false
    private val CHANNEL_ID = "door_status_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        timerText = findViewById(R.id.timerText)
        val openButton: Button = findViewById(R.id.openButton)
        val closeButton: Button = findViewById(R.id.closeButton)

        openButton.setOnClickListener {
            if (!isTimerRunning) {
                relayRef.setValue("open")
                statusText.text = "Status: Open"
                startTimer()
            }
        }

        closeButton.setOnClickListener {
            relayRef.setValue("close")
            statusText.text = "Status: Closed"
            stopTimer()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(30000, 1000) { // 30 seconds, 1-second intervals
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerText.text = "Time left: $secondsLeft s"
            }

            override fun onFinish() {
                showPopupWindow()
                timerText.text = ""
                isTimerRunning = false
            }
        }
        countDownTimer.start()
        isTimerRunning = true
    }

    private fun stopTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel()
            timerText.text = ""
            isTimerRunning = false
        }
    }

    private fun showPopupWindow() {
        val popupView = layoutInflater.inflate(R.layout.custom_popup_window, null)

        // Create the PopupWindow
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.elevation = 10f

        // Handle button actions
        val btnCloseDoor: Button = popupView.findViewById(R.id.btnCloseDoor)
        val btnIgnore: Button = popupView.findViewById(R.id.btnIgnore)

        btnCloseDoor.setOnClickListener {
            // Handle Close Door action
            relayRef.setValue("close")
            statusText.text = "Status: Closed"
            Toast.makeText(this, "Door closed", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        btnIgnore.setOnClickListener {
            // Handle Ignore action
            Toast.makeText(this, "Action ignored", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        // Show the PopupWindow (e.g., center of the screen)
        popupWindow.showAtLocation(
            findViewById(R.id.main_layout), // Parent view (could be any view)
            Gravity.CENTER, // Positioning
            0, 0
        )
    }
}
