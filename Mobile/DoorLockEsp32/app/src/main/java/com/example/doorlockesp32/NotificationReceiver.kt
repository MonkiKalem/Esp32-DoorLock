package com.example.doorlockesp32

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val relayRef = FirebaseDatabase.getInstance().getReference("relay")

        when (action) {
            "CLOSE_DOOR" -> {
                relayRef.setValue("close")
                Toast.makeText(context, "Door closed", Toast.LENGTH_SHORT).show()
            }
            "IGNORE" -> {
                Toast.makeText(context, "Notification ignored", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
