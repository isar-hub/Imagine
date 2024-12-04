package com.isar.imagine.all_products.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.isar.imagine.all_products.models.data.BilledProductInfo
import com.isar.imagine.all_products.models.data.SoldProductInfo
import com.isar.imagine.all_products.models.repo.AllProductRepo
import com.isar.imagine.inventory.data.InventoryData
import com.isar.imagine.inventory.data.repos.InventoryRepository
import com.isar.imagine.inventory.viewmodel.InventoryViewModel
import com.isar.imagine.utils.Results
import kotlinx.coroutines.launch

class AllProductViewModel(val repository: AllProductRepo) : ViewModel() {


    private val _soldProductLiveData = MutableLiveData<Results<List<SoldProductInfo>>>()
    val soldProductLiveData: LiveData<Results<List<SoldProductInfo>>> get() = _soldProductLiveData
    private val _billedProductLiveData = MutableLiveData<Results<List<BilledProductInfo>>>()
    val billedProductLiveData: LiveData<Results<List<BilledProductInfo>>> get() = _billedProductLiveData


    init {
        viewModelScope.launch {
            _soldProductLiveData.value = repository.getAllSoldProducts()
            _billedProductLiveData.value = repository.getAllBilledProducts()
        }
    }



}

class AllProductViewModelFactory(private val repository: AllProductRepo) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AllProductViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}