package com.isar.imagine.transactions.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.billing.TransactionsResponse
import com.isar.imagine.utils.Results
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class TransactionsViewModel(private val transaction: TransactionRepositoryImpl) : ViewModel() {

    private val _transactionsList =
        MutableLiveData<Results<List<TransactionsResponse>>>(Results.Loading())
    val transactionsList: LiveData<Results<List<TransactionsResponse>>> get() = _transactionsList

    init {
        getTransactions()
    }
    fun getTransactions() = viewModelScope.launch {
        _transactionsList.postValue(
            transaction.getTransactions()
        )
    }
}


abstract class TransactionRepository {
    abstract suspend fun getTransactions(): Results<List<TransactionsResponse>>
}


class TransactionRepositoryImpl(val firestore: FirebaseFirestore) : TransactionRepository() {
    override suspend fun getTransactions(): Results<List<TransactionsResponse>> {


        return try {
            val transactions = firestore.collection("transactions").get().await()

            val transactionsList = transactions.documents.mapNotNull {
                Log.e("Transactions", "Result $it.da  and id ${it.id}")
                it.toObject(TransactionsResponse::class.java)?.copy(transactionID = it.id)
            }
            Log.e("Transactions", "Result $transactionsList")
            Results.Success(transactionsList)

        } catch (e: Exception) {
            Results.Error(e.message ?: "Unknown error")
        }

    }


}


class TransactionViewModelFactory(private val repository: TransactionRepositoryImpl) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return TransactionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
