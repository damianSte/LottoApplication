package com.example.lottoapplicationmad.fragments

import android.os.Bundle
import com.example.lottoapplicationmad.R
import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.lottoapplicationmad.firestore.FireStoreData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class BlankFragments : Fragment() {


    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.activity_blank_fragments, container, false)
        val position = arguments?.getInt("position")
        val winText = rootView.findViewById<TextView>(R.id.winTV)
        val backButton = rootView.findViewById<Button>(R.id.backButton)

        backButton.setOnClickListener {
            val activity: FragmentActivity? = activity
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }



        if (position != null) {
            db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list: MutableList<String> = ArrayList()
                        for (document in task.result) {
                            list.add(document.id)
                        }
                        Log.d(ContentValues.TAG, list.toString())


                        if (position < list.size) {
                            db.collection(FirebaseAuth.getInstance().currentUser?.email.toString())
                                .document(list[position])
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    if (documentSnapshot.exists()) {
                                        val dbData = documentSnapshot.toObject(FireStoreData::class.java)
                                        winText.text = dbData?.win.toString()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    // Handle failure
                                    println("Error getting document usersNumbers - " +
                                            "${FirebaseAuth.getInstance().currentUser?.email}: $e")
                                }
                        } else {
                            Log.e(ContentValues.TAG, "Position is out of range")
                        }
                    } else {
                        Log.e(ContentValues.TAG, "Error getting documents: ", task.exception)
                    }
                }
        }

        return rootView
    }

}