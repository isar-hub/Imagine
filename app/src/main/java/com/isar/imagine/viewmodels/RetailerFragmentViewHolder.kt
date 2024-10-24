package com.isar.imagine.viewmodels

import android.app.ProgressDialog
import android.provider.ContactsContract.Groups
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.isar.imagine.Adapters.Retailer
import com.isar.imagine.data.Root
import com.isar.imagine.data.Root2
import com.isar.imagine.utils.Results
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.gson.reflect.TypeToken

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.acl.Group


class RetailerFragmentViewHolder(
    private val repository: RetailerRepository
) : ViewModel() {

    private val functions = Firebase.functions

    private val _inventoryList = MutableLiveData<Results<List<Retailer>>>()
    val inventoryList: LiveData<Results<List<Retailer>>> get() = _inventoryList
    private val retailerList = mutableListOf<Retailer>()

    init {
        Log.e("funtions", "init callled")
        getRetailers()

    }
    fun addRetailer(retailer: Retailer){
        retailerList.add(retailer)
        _inventoryList.postValue(Results.Success(retailerList))
    }

//    fun addLoadingRetailer(){
//        _inventoryList.postValue(Results.Loading())
//    }
//    fun addError(message : String){
//        _inventoryList.postValue(Results.Error(message))
//    }


    private val _userCreated = MutableLiveData<Results<FirebaseUser?>>()
    val userCreated: LiveData<Results<FirebaseUser?>> get() = _userCreated



     fun getRetailers()  {
         viewModelScope.launch {
             val result =repository.getRetailer(functions)

             Log.e("main",result.toString())
//             Log.e("main", result["data"].toString())
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


class RetailerRepository(private val firebaseAuth: FirebaseAuth)  {

//    fun getRetailers() {
//        val url = URL("https://api-jxtqakdgka-uc.a.run.app/main/allRetailers")
//
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val connection = url.openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//
//                val responseCode = connection.responseCode
//                if (responseCode == HttpURLConnection.HTTP_OK) {
//                    val inputStream = BufferedReader(InputStreamReader(connection.inputStream))
//                    val response = StringBuilder()
//                    inputStream.forEachLine { line ->
//                        response.append(line)
//                    }
//                    inputStream.close()
//
//                    withContext(Dispatchers.Main) {
//                        Log.e("API Response", "user : $response")
//                    }
//                } else {
//                    Log.e("API Response", "Failed: HTTP error code $responseCode")
//                }
//                connection.disconnect()
//            } catch (e: Exception) {
//                Log.e("API Response", "error: ${e.message}")
//            }
//        }
//    }
    suspend fun getRetailer(functions: FirebaseFunctions) : List<HashMap<String,String>>? {


        Log.e("functions", "Calling the 'addMessage' function...")
        return  try {
            val result =functions.getHttpsCallable("addmessage").call().await()
           if (result.data != null){
               val data = result.data as List<HashMap<String,String>>
               data;
           }
            else{
                return  null;
           }
        } catch (e: Exception) {
            return  null;
        }
    }



    suspend fun createUser(email: String, password: String, name: String): Results<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).  await()
            val user = authResult.user

            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

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
