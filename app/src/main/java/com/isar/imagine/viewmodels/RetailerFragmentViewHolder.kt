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
import com.isar.imagine.data.Metadata
import com.isar.imagine.data.ProviderDaum
import com.isar.imagine.data.Root2
import com.isar.imagine.utils.Results
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RetailerFragmentViewHolder(
    private val repository: RetailerRepository
) : ViewModel() {

    private val functions = Firebase.functions

    private val _inventoryList = MutableLiveData<Results<List<Retailer>>>()
    val inventoryList: LiveData<Results<List<Retailer>>> get() = _inventoryList

    private val _retailerList = MutableLiveData<Results<List<Root2>>>(Results.Loading())
    val retailerList: LiveData<Results<List<Root2>>> get() = _retailerList


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


class RetailerRepository(private val firebaseAuth: FirebaseAuth) {
    suspend fun getRetailer(functions: FirebaseFunctions): Results<List<Root2>> {
        Log.e("functions", "Calling the 'addMessage' function...")
        return try {
            val result = functions.getHttpsCallable("addmessage").call().await()
            if (result.data != null) {
                val data = result.data as List<HashMap<String, String>>
                Results.Success(data.map { mapToRoot2(it) })
            } else {
                Results.Error("Error in Fetching")
            }
        } catch (e: Exception) {
            return Results.Error("Error ${e.message}")
        }
    }

    fun mapToRoot2(map: Map<String, Any>): Root2 {
        val metadataMap = map["metadata"] as? Map<String, String>
        val providerDataList = map["providerData"] as? List<Map<String, String>>

        return Root2(uid = map["uid"] as String,
            email = map["email"] as String,
            emailVerified = map["emailVerified"] as Boolean,
            displayName = map["displayName"] as String,
            disabled = map["disabled"] as Boolean,

            metadata = Metadata(
                lastSignInTime = metadataMap?.get("lastSignInTime") ?: "",
                creationTime = metadataMap?.get("creationTime") ?: "",
                lastRefreshTime = metadataMap?.get("lastRefreshTime") ?: ""
            ),

            passwordHash = map["passwordHash"] as String,
            passwordSalt = map["passwordSalt"] as String,
            tokensValidAfterTime = map["tokensValidAfterTime"] as String,

            providerData = providerDataList?.map { providerMap ->
                ProviderDaum(
                    uid = providerMap["uid"] ?: "",
                    displayName = providerMap["displayName"] ?: "",
                    email = providerMap["email"] ?: "",
                    providerId = providerMap["providerId"] ?: ""
                )
            } ?: emptyList())
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
