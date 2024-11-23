package com.example.doorlockesp32

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private val database = FirebaseDatabase.getInstance()
    private val relayRef = database.getReference("relay")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        val openButton: Button = findViewById(R.id.openButton)
        val closeButton: Button = findViewById(R.id.closeButton)

        openButton.setOnClickListener {
            relayRef.setValue("open")
            statusText.text = "Status: Open"
        }

        closeButton.setOnClickListener {
            relayRef.setValue("close")
            statusText.text = "Status: Closed"
        }
    }
}
