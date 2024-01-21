package com.example.lottoapplicationmad.firestore

data class FireStoreData(
    var email: String = "",
    var selNumb: List<Int>?=null,
    var drawNumb: List<Int>?=null,
    var win: Double = 0.0,
)