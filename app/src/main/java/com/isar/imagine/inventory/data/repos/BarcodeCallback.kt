package com.isar.imagine.inventory.data.repos

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.utils.CommonMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BarcodeCallback {
    fun onBarcodesPulled(barcodes: List<String>)
    fun onFailure(exception: String)
}

fun pullAndRemoveLimitedBarcodes(firestore: FirebaseFirestore,limit: Int, callback: BarcodeCallback) {

    firestore.runTransaction { transaction ->
        val documentRef = firestore.collection("barcodes").document("LAJnCRdzA0b7RPPvt0WZ")
        CommonMethods.showLogs("firestore","Is avaialale ${documentRef.id}")
        val documentSnapshot = transaction.get(documentRef)

        if (documentSnapshot.exists()) {
            val barcodeList = documentSnapshot.get("barcodes") as? List<String> ?: return@runTransaction
            if (barcodeList.isNotEmpty()) {
                // Get the first 'limit' items
                val barCodes = barcodeList.take(limit)

                // Process the limited barcodes
//                processBarcodes(barCodes)

                // Create a new list excluding the pulled items
                val updatedBarcodeList = barcodeList.drop(limit)

                // Update the Firestore document with the new list
                transaction.update(documentRef, "barcodes", updatedBarcodeList)

                // Return the pulled barcodes through the callback
                callback.onBarcodesPulled(barCodes)
            } else {
                Log.d("Firestore", "No barcodes available in the document.")
                callback.onBarcodesPulled(emptyList())
            }
        } else {
            Log.d("Firestore", "No such document")
            callback.onFailure("No Such Document")
        }
    }.addOnFailureListener { e ->
        Log.w("Firestore", "Transaction failed: $e")
        callback.onFailure(e.message!!)
    }
}

fun pushToFirebase(list: List<String>) {
    val firestore = FirebaseFirestore.getInstance()
    val barcodes = hashMapOf(
        "barcodes" to list
    )
    val docRef = firestore.collection("barcodes").add(barcodes).addOnSuccessListener {
        Log.d("BARCODE", "DocumentSnapshot added with ID: ${it.id}")
    }.addOnFailureListener {
        Log.d("BARCODE", "Error adding document", it)
    }


}