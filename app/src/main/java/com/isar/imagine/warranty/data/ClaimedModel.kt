package com.isar.imagine.warranty.data

data class ClaimedModel(
    var serialNumber: String,
    var reason: String,
    var reasonDescription: String,
    var createdAt: String,
    var status: WarrantyStatus

)

fun Map<String, Any>.fromMap(): ClaimedModel {
    return ClaimedModel(
        serialNumber = this["serialNumber"] as? String ?: "", // Provide default empty string if null
        reason = this["reason"] as? String ?: "", // Provide default empty string if null
        reasonDescription = this["reasonDescription"] as? String ?: "", // Default if null
        createdAt = this["createdAt"] as? String ?: "", // Default if null
        status = WarrantyStatus.valueOf(this["status"] as? String ?: WarrantyStatus.CLAIMED.name) // Default to CLAIMED if null
    )
}

enum class WarrantyStatus { CLAIMED, PROCESSED, COMPLETED }