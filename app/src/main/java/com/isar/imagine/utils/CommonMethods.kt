package com.isar.imagine.utils

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object CommonMethods {

    fun showLogs(tag:String, message : String){
        Log.e(tag,message);
    }

    fun getFireBaseStore() : FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }

    fun getDb():FirebaseFirestore{
        return Firebase.firestore
    }

}