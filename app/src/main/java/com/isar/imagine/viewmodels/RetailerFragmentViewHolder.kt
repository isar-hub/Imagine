package com.isar.imagine.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.isar.imagine.utils.Results
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class RetailerFragmentViewHolder : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRepository = RetailerRepository(firebaseAuth)

    private val _userCreated = MutableLiveData<Results<FirebaseUser?>>()
    val userCreated: LiveData<Results<FirebaseUser?>> get() = _userCreated


    fun createUser(email: String, password: String, name: String) {
        _userCreated.postValue(Results.Loading())  // Set loading state

        viewModelScope.launch {
            val result = userRepository.createUser(email, password, name)
            _userCreated.postValue(result)  // Post success or error result
        }
    }

}


class RetailerRepository(private val firebaseAuth: FirebaseAuth) {

    suspend fun createUser(email: String, password: String, name: String): Results<FirebaseUser?> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).  await()
            val user = authResult.user

            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                val updateResult = user.updateProfile(profileUpdates).await()
                Results.Success(user)  // Successfully created and updated profile
            } else {
                Results.Error("User creation failed")
            }
        } catch (e: Exception) {
            Results.Error(e.message ?: "An error occurred")
        }

    }
}
