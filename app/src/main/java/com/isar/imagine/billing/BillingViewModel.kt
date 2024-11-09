package com.isar.imagine.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isar.imagine.viewmodels.RetailerFragmentViewHolder
import com.isar.imagine.viewmodels.RetailerRepository

class BillingViewModel :ViewModel() {
}

class BillingViewmodelFactory(
    private val repository: RetailerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillingPanelFragment::class.java)) {
            return BillingViewmodelFactory(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
