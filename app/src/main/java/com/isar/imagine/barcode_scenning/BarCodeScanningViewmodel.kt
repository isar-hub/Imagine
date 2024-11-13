package com.isar.imagine.barcode_scenning

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.barcode_scenning.models.BillingDataModel
import com.isar.imagine.barcode_scenning.models.mapToBillingDataModel
import com.isar.imagine.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BarCodeScanningViewmodel : ViewModel() {


    private val _serialNumberLiveData = MutableLiveData<Results<BillingDataModel>>()
    val serialNumberLiveData: LiveData<Results<BillingDataModel>> get() = _serialNumberLiveData


    suspend fun isSerialNumber(barCode: String, firestore: FirebaseFirestore) {
        withContext(Dispatchers.Main) {
            _serialNumberLiveData.value = Results.Loading()

        }
        withContext(Dispatchers.IO) {
            val exists =
                firestore.collection("inventory").whereArrayContains("serialNumber", barCode).get()
                    .await()

            if (exists.documents.isEmpty()) {
                withContext(Dispatchers.Main) {
                    _serialNumberLiveData.value = Results.Error("Not Found")

                }
            } else {

                val barCode1 = exists.documents[0].data?.toMutableMap()
                val itemId = exists.documents[0].id
                barCode1?.let {
                    it["serialNumber"] = barCode
                }
                Log.e("TAG", "barcode data $barCode1")
                val billingData = mapToBillingDataModel(barCode1!!,itemId)
                withContext(Dispatchers.Main) {
                    _serialNumberLiveData.value = Results.Success(billingData)

                }
            }


        }


    }

}

class BarCodeScanningViewModelProvider(private val firestore: FirebaseFirestore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BarCodeScanningViewmodel::class.java)) {
            return BarCodeScanningViewModelProvider(firestore) as T

        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
