package com.isar.imagine.inventory.data.repos

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.inventory.data.InventoryData
import com.isar.imagine.utils.Brand
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.Data
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class InventoryRepositoryImpl(private val firestore: FirebaseFirestore) : InventoryRepository {

    private suspend fun getSingleBrand(brandName: String): Brand {
        val snapshot = Data().brands
        return snapshot.find {
            it.name == brandName
        } ?: Brand()
    }

    override suspend fun getBrands(): List<String> {
        val snapshot = Data().brands
        return snapshot.map { it.name }
    }

    override suspend fun getModels(
        brandId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val snapshot = getSingleBrand(brandId).models
            if (snapshot.isNotEmpty()) {
                val url = snapshot.map { it.imageUrl }
                onSuccess(snapshot.map { it.name })
            } else {
                onFailure(Exception("No Model Present"))
            }
        } catch (e: Exception) {
            CommonMethods.showLogs("Firestore", "Models are ${e.message}")
            onFailure(e)
        }

    }

    override suspend fun getVariants(
        brandId: String,
        modelName: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        CommonMethods.showLogs("Firebase", "Brand name $brandId mode $modelName")
        try {

            val snapshot = getSingleBrand(brandId).models.find { it.name == modelName }!!.variants
            CommonMethods.showLogs("Firebase", "Brand name $snapshot")
            if (snapshot.isNotEmpty()) {
                onSuccess(snapshot.map { it.variant })

            } else {
                onFailure(Exception("No Model Present"))
            }
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override suspend fun getImage(
        brandId: String,
        modelName: String,
    ): String {
        val snapshot = getSingleBrand(brandId).models.find { it.name == modelName }!!.imageUrl
        return snapshot
    }

    override suspend fun saveInventory(item: List<InventoryData>, result: (Boolean) -> Unit) =
        coroutineScope {
            try {
                val batch = firestore.batch()


                item.forEach { item ->
                    val inventoryRef = firestore.collection("inventory").document()
                    batch.set(inventoryRef, item)
                }

                batch.commit().await() // Commits the batch, all-or-nothing
                result(true)
            } catch (e: Exception) {
                Log.e("Inventory", "Batch save failed: ${e.message}")
                result(false)
            }
        }

//        var response = ""
//
//        try {
//            // Generate a document reference for the new inventory item
//            val inventoryRef = firestore.collection("inventory").document()
//            inventoryRef.set(item).addOnSuccessListener {
//                // Handle success (e.g., show a toast)
//                response = "Posted"
//            }.addOnFailureListener { e ->
//                // Handle failure (e.g., show an error message)
//                response = "${e.message}"
//            }
//        } catch (e: Exception) {
//            response = "${e.message}"
//        }
//        return response
//    }


}