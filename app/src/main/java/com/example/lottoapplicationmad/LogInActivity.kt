package com.example.lottoapplicationmad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth

class LogInActivity : ErrorBar() {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in_activity_main)

        inputEmail = findViewById(R.id.EtEmail)
        inputPassword = findViewById(R.id.editTextPasswordregister)
        val loginButton = findViewById<Button>(R.id.LogInBttn)
        val textViewRegister = findViewById<View>(R.id.textViewRegister)

        loginButton?.setOnClickListener {
            logInRegisteredUser()
        }

        // go to register user class
        textViewRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    private fun validateLoginDetails(): Boolean {
        // getting email and password trimming spaces
        val email = inputEmail?.text.toString().trim()
        val password = inputPassword?.text.toString().trim()

        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("User not found", true)
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Invalid Password or Email", true)
                false
            }

            else -> {
                showErrorSnackBar("Your details are valid", false)
                true
            }
        }
    }

    private fun logInRegisteredUser() {


        if (validateLoginDetails()) {
            val email = inputEmail?.text.toString().trim() { it <= ' ' }
            val password = inputPassword?.text.toString().trim() { it <= ' ' }

            //Log-in using FirebaseAuth

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToNumbSelectionActivity()
                        finish()

                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    open fun goToNumbSelectionActivity() {

        val user = FirebaseAuth.getInstance().currentUser;
        val uid = user?.email.toString()

        val intent = Intent(this, NumbSelectionActivity::class.java)
        intent.putExtra("uID", uid)
        startActivity(intent)
    }

}

