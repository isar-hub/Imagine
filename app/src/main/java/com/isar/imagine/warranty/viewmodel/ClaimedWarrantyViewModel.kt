package com.isar.imagine.warranty.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.utils.Results
import com.isar.imagine.warranty.data.ClaimedModel
import com.isar.imagine.warranty.data.fromMap


class ClaimedWarrantyViewModel : ViewModel() {

    val firestore = FirebaseFirestore.getInstance()
    private val _warranty: MutableLiveData<Results<List<ClaimedModel>>> = MutableLiveData()
    val warranty: LiveData<Results<List<ClaimedModel>>> get() = _warranty


    init {
        fetchWarranties()
    }
    private fun fetchWarranties() {
        Log.d("FetchWarranties", "Fetching warranties...")

        // Start the process
        _warranty.value = Results.Loading()
        Log.d("FetchWarranties", "Set loading state")

        firestore.collection("products") // Collection name
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FetchWarranties", "Successfully fetched documents")

                    val warrantiesList: MutableList<ClaimedModel> = mutableListOf()

                    // Iterating over the fetched documents
                    for (document in task.result!!) {
                        Log.d("FetchWarranties", "Processing document: ${document.id}")

                        val warranties = document["warranty"] as List<Map<String, Any>>?

                        // Check if warranties data exists
                        if (warranties != null && warranties.isNotEmpty()) {
                            Log.d("FetchWarranties", "Found ${warranties.size} warranties in document")

                            warranties.forEach {
                                // Logging each warranty being added to the list
                                Log.d("FetchWarranties", "Processing warranty: $it")
                                warrantiesList.add(it.fromMap().copy(serialNumber = document.id))
                            }
                        } else {
                            Log.d("FetchWarranties", "No warranties found for this document")
                        }
                    }

                    // After processing all warranties, setting them to LiveData
                    Log.d("FetchWarranties", "Finished processing warranties, setting to LiveData")
                    _warranty.value = Results.Success(warrantiesList)

                } else {
                    Log.w("FetchWarranties", "Error getting documents.", task.exception)
                    // If there is an error, set the error message
                    _warranty.value = Results.Error(task.exception?.localizedMessage ?: "Error fetching data")
                }
            }
    }
}