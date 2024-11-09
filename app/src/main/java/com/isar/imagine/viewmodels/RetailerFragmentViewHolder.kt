package com.isar.imagine.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.isar.imagine.Adapters.Retailer
import com.isar.imagine.data.Root2
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.Results
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RetailerFragmentViewHolder(
    private val repository: RetailerRepository
) : ViewModel() {

    private val functions = Firebase.functions

    private val _inventoryList = MutableLiveData<Results<List<Retailer>>>()
    val inventoryList: LiveData<Results<List<Retailer>>> get() = _inventoryList

    private val _retailerList = MutableLiveData<Results<List<RetailerEntity>>>(Results.Loading())
    val retailerList: LiveData<Results<List<RetailerEntity>>> get() = _retailerList


    init {
        getRetailers()
    }

    private val _userCreated = MutableLiveData<Results<FirebaseUser?>>()
    val userCreated: LiveData<Results<FirebaseUser?>> get() = _userCreated


    fun getRetailers() {
        viewModelScope.launch {
            val result = repository.getRetailer(functions)
            _retailerList.postValue(result)
        }
    }


    fun createUser(email: String, password: String, name: String) {
        _userCreated.postValue(Results.Loading())  // Set loading state

        viewModelScope.launch {
            val result = repository.createUser(email, password, name)
            _userCreated.postValue(result)  // Post success or error result
        }
    }

}


class RetailerRepository(
    private val firebaseAuth: FirebaseAuth,
    private val retailerDao: RetailerDao
) {
    suspend fun getRetailer(functions: FirebaseFunctions): Results<List<RetailerEntity>> {
        Log.e("functions", "Calling the 'addMessage' function...")

        val localData = retailerDao.getAllRetailers()
        if (localData.isNotEmpty()) {
            CommonMethods.showLogs("TAG","Coming from local $localData")
            return Results.Success(localData)

        } else {
            return try {
                val result = functions.getHttpsCallable("addmessage").call().await()
                if (result.data != null) {
                    val data = result.data as List<HashMap<String, String>>
                    val retailerEntities = data.map { mapToRoot2(it) }
                    retailerDao.insertRetailers(retailerEntities)
                    Results.Success(retailerEntities)
                } else {
                    Results.Error("Error in Fetching")
                }
            } catch (e: Exception) {
                return Results.Error("Error ${e.message}")
            }
        }

    }

    fun mapToRoot2(map: Map<String, Any>): RetailerEntity {

        return RetailerEntity(
            uid = map["uid"] as String,
            email = map["email"] as String,
            displayName = map["displayName"] as String,
            disabled = map["disabled"] as Boolean,
        )
    }

    suspend fun createUser(email: String, password: String, name: String): Results<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(name).build()

                user.updateProfile(profileUpdates).await()

                Results.Success(user)  // Successfully created and updated profile
            } else {
                Results.Error("User creation failed")
            }
        } catch (e: Exception) {
            Results.Error(e.message ?: "An error occurred")
        }

    }
}
