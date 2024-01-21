package com.example.lottoapplicationmad

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.example.lottoapplicationmad.firestore.FireStoreClass
import com.example.lottoapplicationmad.firestore.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : ErrorBar() {

    private var registerEmail: EditText? = null
    private var registerName: EditText? = null
    private var registerPassword: EditText? = null
    private var registerRepeatPassword: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        var registerButton: Button = findViewById(R.id.SignUpButton)
        registerButton.isEnabled = false

        val switchView = findViewById<Switch>(R.id.AgeSwitch)
        switchView.setOnCheckedChangeListener { _, isChecked ->
            registerButton.isEnabled = isChecked
            switchView.text = "YES"
            if (!isChecked)
                switchView.text = "NO"
        }


        registerEmail = findViewById(R.id.editTextEmailAddressRegsiter)
        registerName = findViewById(R.id.editTextNameRegister)
        registerPassword = findViewById(R.id.editTextPasswordRegister)
        registerRepeatPassword = findViewById(R.id.editTextPasswordRepeatRegister)

        registerButton?.setOnClickListener {
            validateRegistration()
            registerUser()

        }
    }

    private fun validateRegistration(): Boolean {
        val email = registerEmail?.text.toString().trim()
        val name = registerName?.text.toString().trim()
        val password = registerPassword?.text.toString().trim()
        val repeatPassword = registerRepeatPassword?.text.toString().trim()

        // if all values are filled
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Email is required.", true)
                false
            }
            // email must be valid according to isValidEmail
            !isValidEmail(email) -> {
                showErrorSnackBar("Invalid email format.", true)
                false
            }

            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Name is required.", true)
                false
            }
            // checking if name has numbers according to containsNumbers function
            containsNumbers(name) -> {
                showErrorSnackBar("Name must not contain numbers.", true)
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Password is required.", true)
                false
            }

            TextUtils.isEmpty(repeatPassword) -> {
                showErrorSnackBar("Repeat password is required.", true)
                false
            }

            password != repeatPassword -> {
                showErrorSnackBar("Passwords do not match.", true)
                false
            }

            else -> true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    // Function checks if name has any Numbers
    private fun containsNumbers(name: String): Boolean {
        return name.any { it.isDigit() }
    }

    fun goToLogin(view: View) {
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }

    //    private fun registerUser(){
//        if (validateRegistration()){
//            val login: String = registerEmail?.text.toString().trim() {it <= ' '}
//            val password: String = registerPassword?.text.toString().trim() {it <= ' '}
//            val name: String = registerName?.text.toString().trim() {it <= ' '}
//
//
//            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login,password).addOnCompleteListener(
//                OnCompleteListener <AuthResult>{ task ->
//
//                    if(task.isSuccessful){
//                        val firebaseUser: FirebaseUser = task.result!!.user!!
//                        showErrorSnackBar("You are registered successfully. Your user id is ${firebaseUser.uid}",false)
//
//                        val user = User("Testowe ID",
//                            name,
//                            true,
//                            login,
//                        )
//                        FireStoreClass().registerUserFS(this@RegisterActivity, user)
//
//                        FirebaseAuth.getInstance().signOut()
//                        finish()
//
//
//                    } else{
//                        showErrorSnackBar(task.exception!!.message.toString(),true)
//                    }
//
//                }
//            )
//
//        }
//    }
//
//    fun userRegistrationSuccess() {
//        Toast.makeText(
//            this@RegisterActivity, resources.getString(R.string.register_success),
//            Toast.LENGTH_LONG
//        ).show()
//    }
//
//}
    private fun registerUser() {
        if (validateRegistration()) {
            val login: String = registerEmail?.text.toString().trim(' ')
            val password: String = registerPassword?.text.toString().trim(' ')
            val name: String = registerName?.text.toString().trim(' ')
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            showErrorSnackBar(
                                "You are registered successfully. Your user id is ${firebaseUser.uid}",
                                false
                            )

                            val user = User(
                                "ID",
                                name,
                                true,
                                login,
                            )
                            FireStoreClass().registerUserFS(this@RegisterActivity, user)
                            FirebaseAuth.getInstance().signOut()
                            finish()
                        } else {
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
                )
        }
    }

    fun userRegistrationSuccess() {
        Toast.makeText(
            this@RegisterActivity, resources.getString(R.string.register_success),
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        fun goToLogin(registerActivity: RegisterActivity, view: View) {
            val intent = Intent(registerActivity, LogInActivity::class.java)
            registerActivity.startActivity(intent)
            registerActivity.finish()
        }
    }
}