package com.example.lottoapplicationmad

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import com.example.lottoapplicationmad.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class SelectedTime : AppCompatActivity() {

    private lateinit var selectedTimeTextView: TextView
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_time)

        firestore = FirebaseFirestore.getInstance()

        val timePicker = findViewById<TimePicker>(R.id.timePicker)

        val pickTimeButton = findViewById<Button>(R.id.saveBtn)

        val goBack = findViewById<Button>(R.id.buttonGoBack)

        pickTimeButton.setOnClickListener {
            // Get the selected time from the TimePicker
            val selectedHour = timePicker.hour
            val selectedMinute = timePicker.minute

            // Format the selected time as a string
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

            if (selectedTime.isNotEmpty()) {
                saveSelectedTimeToFirestore(selectedTime)

                // Schedule the alarm/notification based on the selected time
                scheduleNotification(selectedTime)
            } else {
                showTimePickerDialog()
            }
        }

        goBack.setOnClickListener {
            val intent = Intent(this, NumbSelectionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val selectedTimeText = String.format(
                    "%02d:%02d",
                    selectedHour,
                    selectedMinute
                )
                selectedTimeTextView.text = selectedTimeText
            },
            hourOfDay,
            minute,
            true // Set to true if you want to use the 24-hour format
        )

        timePickerDialog.show()
    }

    private fun saveSelectedTimeToFirestore(selectedTime: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email.toString()

        userId?.let {
            firestore.collection(email)
                .document("NotificationTime")
                .set(mapOf("selected_time" to selectedTime))
                .addOnSuccessListener {
                    // Handle success if needed
                }
                .addOnFailureListener {
                    // Handle failure if needed
                }
        }
    }

    private fun scheduleNotification(selectedTime: String) {
        val intentNot = Intent(this, ReminderBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intentNot,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()

        // Parse selected time to set the notification delay
        val selectedTimeParts = selectedTime.split(":")
        calendar.set(Calendar.HOUR_OF_DAY, selectedTimeParts[0].toInt())
        calendar.set(Calendar.MINUTE, selectedTimeParts[1].toInt())

        val selectedTimeMillis = calendar.timeInMillis

        // Calculate the delay based on the selected time
        val delayMillis = selectedTimeMillis - System.currentTimeMillis()

        // Set the alarm with the calculated delay
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + delayMillis,
            pendingIntent
        )
    }
}
