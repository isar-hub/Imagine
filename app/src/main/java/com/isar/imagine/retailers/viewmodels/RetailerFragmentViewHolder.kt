package com.isar.imagine.retailers.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.isar.imagine.retailers.views.Retailer
import com.isar.imagine.retailers.data.Notifications
import com.isar.imagine.retailers.data.UserDetails
import com.isar.imagine.retailers.data.UserDetailsDTO
import com.isar.imagine.retailers.data.dao.UserDao
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.Results
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RetailerFragmentViewHolder(
    private val repository: RetailerRepository
) : ViewModel() {

    private val functions = Firebase.functions
    private val firestore = FirebaseFirestore.getInstance()

    private val _inventoryList = MutableLiveData<Results<List<Retailer>>>()
    val inventoryList: LiveData<Results<List<Retailer>>> get() = _inventoryList

    private val _retailerList = MutableLiveData<Results<List<UserDetails>>>(Results.Loading())
    val retailerList: LiveData<Results<List<UserDetails>>> get() = _retailerList


    init {
        getRetailers()
    }


    private val _userCreated = MutableLiveData<Results<FirebaseUser?>>()
    val userCreated: LiveData<Results<FirebaseUser?>> get() = _userCreated


    fun getRetailers() {
        viewModelScope.launch {

            val result = repository.getRetailer(firestore)
            _retailerList.postValue(result)
        }
    }

    fun sendNotification(
        title: String, message: String, callback: (Results<String>) -> Unit
    ) {
        viewModelScope.launch {
            val notifications = Notifications(title, message)
            val result = repository.sendNotification(firestore, notifications)
            callback(result)
        }
    }

    fun createUser(
        email: String, password: String, userDetails: UserDetails
    ) {
        _userCreated.postValue(Results.Loading())  // Set loading state

        viewModelScope.launch {
            val result = repository.createUser(email, password)

            if (result is Results.Success) {
                userDetails.uid = result.data?.uid.toString()
                val posted = repository.saveUserDetails(userDetails, firestore)
                if (posted.data?.first == true) {
                    _userCreated.postValue(result)  // Post success result
                } else {
                    result.data?.delete()
                    _userCreated.postValue(Results.Error(posted.message!!))
                }
            } else {
                result.data?.delete()
                _userCreated.postValue(result)
            }
        }
    }

}


class RetailerRepository(
    private val firebaseAuth: FirebaseAuth, private val retailerDao: UserDao
) {

    suspend fun sendNotification(
        firestore: FirebaseFirestore, notifications: Notifications
    ): Results<String> {
        return try {
            // Add the notification to the "notification" collection
            firestore.collection("notification").document().set(notifications).await()

            // If successful, return a success result
            Results.Success("Notification sent successfully")
        } catch (e: Exception) {
            // Handle exceptions and return an error result
            Results.Error("Error in sending notification: ${e.message}")
        }
    }

    suspend fun getRetailer(functions: FirebaseFirestore): Results<List<UserDetails>> {
        Log.e("functions", "Calling the 'addMessage' function...")

//        val localData = retailerDao.getAllRetailers()
//        if (localData.isNotEmpty()) {
//            CommonMethods.showLogs("TAG", "Coming from local $localData")
//            return Results.Success(localData)
//
//        }
//        else {
        return try {
            val result = functions.collection("users").get().await()
            if (result != null) {
                val obj = result.documents.map { it.toObject(UserDetailsDTO::class.java) }
                Log.e("USERS", "TO OBJECT $obj")
                val retailerEntities = obj.map { it?.toUserDetails()!! }
//                retailerDao.insertRetailers(retailerEntities)
                Results.Success(retailerEntities)
            } else {
                Results.Error("Error in Fetching")
            }
        } catch (e: Exception) {
            return Results.Error("Error ${e.message}")
        }
        //}

    }


    suspend fun createUser(email: String, password: String): Results<FirebaseUser?> {
        return try {
            val authResult =
                firebaseAuth.createUserWithEmailAndPassword("$email@imagine.com", password).await()
            val user = authResult.user
            if (user != null) {
                Results.Success(user)
            } else {
                Results.Error("User creation failed")
            }
        } catch (e: Exception) {
            Results.Error(e.message ?: "An error occurred")
        }

    }

    suspend fun saveUserDetails(
        userDetails: UserDetails, firestore: FirebaseFirestore
    ): Results<Pair<Boolean, String>> {
        return try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {

                val documentReference = firestore.document("users/${user.uid}")
                documentReference.set(userDetails).await()
                Results.Success(Pair(true, "Document successfully written"))
            } else {
                // Handle case where the user is not authenticated
                Results.Error("User is not logged in")
            }
        } catch (e: Exception) {
            CommonMethods.showLogs("TAG", "Error writing document ${e.message}")
            Results.Error("Error writing document: ${e.message}")
        }
    }

}
