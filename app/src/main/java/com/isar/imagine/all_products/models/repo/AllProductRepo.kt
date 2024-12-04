package com.isar.imagine.all_products.models.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.all_products.models.data.BilledProductInfo
import com.isar.imagine.all_products.models.data.SoldProductInfo
import com.isar.imagine.billing.SingleItem
import com.isar.imagine.billing.Status
import com.isar.imagine.utils.Results
import kotlinx.coroutines.tasks.await

class AllProductRepo(val firestore: FirebaseFirestore) : AllProductInterface {
    override suspend fun getAllSoldProducts(): Results<List<SoldProductInfo>> {
        val allProduct =
            firestore.collection("products").whereEqualTo("status", "SOLD").get().await()

        return if (!allProduct.isEmpty) {
            Results.Success(allProduct.documents.mapNotNull { document ->
                document.toObject(SoldProductInfo::class.java)
                    ?.copy(status = document.data?.get("status")?.let {
                            runCatching { Status.valueOf(it.toString()) }.getOrElse { Status.BILLED }
                        } ?: Status.BILLED)
            })
        } else {
            Results.Error("Error : No Items Available")
        }
    }

    override suspend fun getAllBilledProducts(): Results<List<BilledProductInfo>> {
        val allProduct =
            firestore.collection("products").whereEqualTo("status", "BILLED").get().await()

        return if (!allProduct.isEmpty) {
            Results.Success(allProduct.documents.map { document -> document.toObject(BilledProductInfo::class.java)!! })
        } else {
            Results.Error("Error : No Items Available")
        }
    }

}

interface AllProductInterface {
    suspend fun getAllSoldProducts(): Results<List<SoldProductInfo>>
    suspend fun getAllBilledProducts(): Results<List<BilledProductInfo>>
}