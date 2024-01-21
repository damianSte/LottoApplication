package com.example.lottoapplicationmad

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.google.firebase.Firebase
import com.example.lottoapplicationmad.firestore.FireStoreData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.util.Random

class NumbDrawingActivity : ErrorBar() {

    val db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        val formattedDateTime = intent.getStringExtra("DATETIME")


        var score = 0
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_numb_drawing)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val drawingButton = findViewById<Button>(R.id.buttonGenerate)

        progressBar.max = 6
        val delayMillis = 1000L
        val handler = Handler(Looper.getMainLooper())
        val statisticsBtn = findViewById<Button>(R.id.buttonStatistics)
        statisticsBtn.isEnabled = false

        val buttons = listOf(
            findViewById<Button>(R.id.button),
            findViewById<Button>(R.id.button2),
            findViewById<Button>(R.id.button3),
            findViewById<Button>(R.id.button4),
            findViewById<Button>(R.id.button5),
            findViewById<Button>(R.id.button6)
        )

        var selectedNumbers: IntArray? = IntArray(6)

        if (formattedDateTime != null) {
            db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                .document(formattedDateTime)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val dbData = documentSnapshot.toObject(FireStoreData::class.java)
                        selectedNumbers=dbData?.selNumb?.toIntArray()

                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    println("Error getting document usersNumbers - " +
                            "${FirebaseAuth.getInstance().currentUser?.email}: $e")
                }
        }

        for (button in buttons) {
            button.visibility = View.INVISIBLE
        }

        drawingButton.setOnClickListener {

            drawingButton.isEnabled=false

            Thread {
                var progressStatus = 0
                val drawingNumbs = lotto()

                for (button in buttons) {
                    progressStatus += 1
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post {
                        progressBar.progress = progressStatus
                        button.text = drawingNumbs[progressStatus-1].toString()
                        if(selectedNumbers?.contains(drawingNumbs[progressStatus-1])==true){
                            button.setBackgroundColor(Color.GREEN)
                            button.setTextColor(Color.WHITE)
                            score++
                        } else{
                            button.setBackgroundColor(Color.RED)
                            button.setTextColor(Color.WHITE)
                        }

                        button.visibility = View.VISIBLE

                    }
                    try {
                        // Sleep for 1000 milliseconds.
                        Thread.sleep(delayMillis)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                runOnUiThread {

                    val winsNumb=simPlayers(doubleArrayOf(7.2e-8,1.8e-5,0.00097,0.077))
                    val win = calculateWin(kotlin.random.Random.nextDouble(0.0,50e6),
                        winsNumb, score)


                    val updates = mapOf(
                        "win" to win,
                        "drawNumb" to drawingNumbs.toList()
                    )

                    if (formattedDateTime != null) {
                        db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                            .document(formattedDateTime)
                            .update(updates)
                            .addOnSuccessListener {
                                // Handle success
                                println("Document updated successfully in usersNumbers" +
                                        "/${FirebaseAuth.getInstance().currentUser?.email}")
                            }
                            .addOnFailureListener { e ->
                                // Handle failure
                                println("Error updating document in usersNumbers" +
                                        "/${FirebaseAuth.getInstance().currentUser?.email}: $e")
                            }
                    }
                    statisticsBtn.isEnabled = true


                    showErrorSnackBar(
                        "You win: $win $",
                        errorMessage = false
                    )
                    drawingButton.isEnabled=true

                }
            }.start()
        }

        statisticsBtn.setOnClickListener(){
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }


    }

    fun simPlayers(probabilityArray: DoubleArray, numberOfPopulation: Int = 3800): IntArray {
        val numberOfPlayers = Random().nextInt(numberOfPopulation)
        val numberOfWins = IntArray(4)
        var iterator = 0
        for (probability in probabilityArray) {
            var numberOfWinningPlayers = 0

            repeat(numberOfPlayers) {
                val randomValue = kotlin.random.Random.nextDouble(0.0, 1.0)
                if (randomValue < probability) {
                    numberOfWinningPlayers++
                }
            }
            numberOfWins[iterator] = numberOfWinningPlayers
            iterator++
            numberOfWinningPlayers = 0
        }
        return numberOfWins
    }

    fun lotto(n: Int = 6, m: Int = 49): IntArray {
        var numbersList = ArrayList<Int>()
        if (m < n) {
            println("Range cannot be smaller than array size")
            return IntArray(n)
        }
        while (numbersList.size < n) {
            val number = (1..49).random()
            if (number !in numbersList) {
                numbersList.add(number)
            }
        }

        return numbersList.toIntArray()
    }

    fun calculateWin(cummulate: Double = 0.0, winners: IntArray, score: Int = 0): Double {
        when (score) {
            6 -> return (cummulate * 0.44) / (winners[0] + 1)
            5 -> return (cummulate * 0.08) / winners[1] + 1
            4 -> return (cummulate * 0.48) / winners[2] + 1
            3 -> return (cummulate * 0.48) / winners[3] + 1
            else -> return 0.0
        }
    }

}
