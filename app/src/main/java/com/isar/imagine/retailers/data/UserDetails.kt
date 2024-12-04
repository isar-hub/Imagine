package com.isar.imagine.retailers.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.isar.imagine.utils.Utils.now
import java.io.Serializable

@Entity(tableName = "Retailers")
data class UserDetails(
    @PrimaryKey var uid: String,
    val name: String,
    val email: String,
    val mobile: String,
    val companyName : String?,
    val gstNumber: String?,
    val userName : String,
    val password: String,
    val address: String,
    val city: String,
    val role : UserRole,
    val state: String,
    val pinCode: Int,
    val image: String?,
    val createdAt: String = now()
): Serializable

enum class UserRole(val displayName: String) {
    RETAILER("Retailer"),
    DISTRIBUTOR("Distributor");

    companion object {
        fun fromString(value: String): UserRole? {
            return entries.find { it.displayName == value }
        }
    }
}

data class UserDetailsDTO(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val companyName: String? = null,
    val gstNumber: String? = null,
    val userName: String = "",
    val password: String = "",
    val address: String = "",
    val city: String = "",
    val role: UserRole = UserRole.RETAILER,
    val state: String = "",
    val pinCode: Int = 0,
    val image: String? = null,
) {
    fun toUserDetails(): UserDetails {
        return UserDetails(
            uid = uid,
            name = name,
            email = email,
            mobile = mobile,
            companyName = companyName,
            gstNumber = gstNumber,
            userName = userName,
            password = password,
            address = address,
            city = city,
            role = role,
            state = state,
            pinCode = pinCode,
            image = image,
        )
    }
}

data class Notifications(val title : String, val message : String, val createdAt: String = now())