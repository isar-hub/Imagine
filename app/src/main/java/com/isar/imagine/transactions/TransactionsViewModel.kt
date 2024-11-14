package com.isar.imagine.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.inventory.InventoryRepository

class TransactionsViewModel (private val repository: InventoryRepository) : ViewModel() {

    private val _transactionList = MutableLiveData<List<InventoryItem>>(emptyList())
    val transactionList : LiveData<List<InventoryItem>> get() = _transactionList

    // adding item to show a list of inventory item
    fun addItem(
        name: String,
        model: String,
        variant: String,
        condition: String,
        purchasePrice: Double,
        sellingPrice: Double,
        quantity: Int,
        notes: String
    ) {


        //new item of inventory item with method parameters
        val newItem = InventoryItem(
            quantity,name, model,  variant, condition, purchasePrice, sellingPrice,  notes
        )

        //temp list of inventory item
        val updatedList = _transactionList.value?.toMutableList() ?: mutableListOf()

        // Check for duplicates and increase quantity if exists
        val existingItem = updatedList.find {
            it.brand == name && it.model == model && it.variant == variant
        }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            updatedList.add(newItem)
        }

        //adding temp list to our inventory item to show in list view of inventory item
        _transactionList.value = updatedList
    }
}