package com.isar.imagine.inventory.data.repos

import com.isar.imagine.inventory.data.InventoryData


interface InventoryRepository {
    suspend fun getBrands(): List<String>
    suspend fun getModels(
        brandId: String, onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit
    )

    suspend fun getVariants(
        brandId: String,
        modelName: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (Exception) -> Unit
    )

    suspend fun getImage(
        brandId: String,
        modelName: String,
    ): String

    suspend fun saveInventory(item: List<InventoryData>, result: (Boolean) -> Unit)
}

