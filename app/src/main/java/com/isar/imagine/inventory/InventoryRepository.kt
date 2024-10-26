package com.isar.imagine.inventory

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.inventory.models.DataClass
import kotlinx.coroutines.tasks.await

interface InventoryRepository {
    suspend fun getBrands(): List<DataClass.Brand>
    suspend fun getModels(brandId: String): List<DataClass.Model>
    suspend fun getVariants(brandId: String, modelId: String): List<DataClass.Variant>
    suspend fun saveInventory(item: DataClass.InventoryData): Boolean
}

class InventoryRepositoryImpl(private val firestore: FirebaseFirestore) : InventoryRepository {
    override suspend fun getBrands(): List<DataClass.Brand> {
        val brands = mutableListOf<DataClass.Brand>()
        try {
            val snapshot = firestore.collection("brands").get().await()
            for (doc in snapshot.documents) {
                val brandName = doc.getString("name") ?: ""
                brands.add(DataClass.Brand(id = doc.id, name = brandName))
            }
        } catch (e: Exception) {
            // Handle exception (log or rethrow)
            throw e
        }
        return brands
    }

    override suspend fun getModels(brandId: String): List<DataClass.Model> {
        val models = mutableListOf<DataClass.Model>()
        try {
            val snapshot =
                firestore.collection("brands").document(brandId).collection("models").get().await()
            for (doc in snapshot.documents) {
                val modelName = doc.getString("name") ?: ""
                models.add(DataClass.Model(id = doc.id, name = modelName))
            }
        } catch (e: Exception) {
            throw e
        }
        return models
    }

    override suspend fun getVariants(brandId: String, modelId: String): List<DataClass.Variant> {
        val variants = mutableListOf<DataClass.Variant>()
        try {
            val snapshot = firestore.collection("brands").document(brandId).collection("models")
                .document(modelId).collection("variants").get().await()
            for (doc in snapshot.documents) {
                val ram = doc.getLong("ram") ?: 0
                val rom = doc.getLong("rom") ?: 0
                val color = doc.getString("color") ?: ""
                variants.add(
                    DataClass.Variant(
                        id = doc.id,
                        ram = ram,
                        rom = rom,
                        color = color,
                    )
                )
            }
        } catch (e: Exception) {
            throw e
        }
        return variants
    }

    override suspend fun saveInventory(item: DataClass.InventoryData): Boolean {

        var response = false;

        try {
            // Generate a document reference for the new inventory item
            val inventoryRef = firestore.collection("inventory").document()
            inventoryRef.set(item).addOnSuccessListener {
                    // Handle success (e.g., show a toast)
                response = true
                }.addOnFailureListener { e ->
                    // Handle failure (e.g., show an error message)
                response = false
                }
        } catch (e: Exception) {
            response = false
        }
        return response
    }


}