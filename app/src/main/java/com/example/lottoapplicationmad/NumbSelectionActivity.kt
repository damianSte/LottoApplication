package com.example.lottoapplicationmad

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import com.example.lottoapplicationmad.firestore.FireStoreData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class NumbSelectionActivity : ErrorBar() {

    val db = Firebase.firestore

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_selection)
        val intent = intent
        val name = intent.getStringExtra("NAME")
        val email = intent.getStringExtra("EMAIL")
        val phone = intent.getStringExtra("PHONE")
        var formattedDateTime: String = ""

        val welcomeText = findViewById<TextView>(R.id.SelectNumbersText)
        welcomeText.text = "Selected Numbers: "

        val numbersText = findViewById<TextView>(R.id.SelectNumbersText)

        val numbersPicker = findViewById<NumberPicker>(R.id.picker)
        numbersPicker.maxValue = 49
        numbersPicker.minValue = 1

        val selectButton = findViewById<Button>(R.id.SelectBtn)
        val getRichButton = findViewById<Button>(R.id.GetRichBtn)

        getRichButton.isEnabled = false

        val selectTimeBtn = findViewById<Button>(R.id.timePickerButton)

        selectTimeBtn.setOnClickListener {
            val intent = Intent(this, SelectedTime::class.java)
            startActivity(intent)
        }

        val numbersArray = IntArray(6)
        var i = 0
        var text = ""



        selectButton.setOnClickListener {
            var selectedNumber = numbersPicker.value
            if (checkSelectedNumber(selectedNumber, numbersArray)) {

                numbersArray[i++] = selectedNumber
                text += selectedNumber.toString()
                text = "$text "
                numbersText.text = text
                if (i > numbersArray.size - 1) {
                    selectButton.isEnabled = false
                    getRichButton.isEnabled = true

                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val email =  FirebaseAuth.getInstance().currentUser?.email.toString()

                    val listOfNumbers =numbersArray.toList()
                    val firebaseData = FireStoreData(email,listOfNumbers,null,0.0)

                    val currentDate = LocalDateTime.now()
                    formattedDateTime = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))


                    GlobalScope.launch(Dispatchers.IO ){
                        userId?.let {
                            db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                                .document(formattedDateTime)
                                .set(firebaseData).await()
                        }
                    }
                }

                showErrorSnackBar(
                    resources.getString(R.string.numbSelectedSuccessful),
                    errorMessage = false
                )

            } else showErrorSnackBar(resources.getString(R.string.numbSelectedError), true)

        }

        getRichButton.setOnClickListener {



            val intent2 = Intent(this, NumbDrawingActivity::class.java)
            intent2.putExtra("DATETIME", formattedDateTime)
            startActivity(intent2)
        }

    }

    fun checkSelectedNumber(number: Int, array: IntArray): Boolean {
        for (element in array) {
            if (element == number) {
                return false
            }
        }
        return true
    }



}
