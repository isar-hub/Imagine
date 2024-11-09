package com.isar.imagine.billing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.isar.imagine.Adapters.InventoryExpandableListAdapter
import com.isar.imagine.R
import com.isar.imagine.barcode_scenning.BarCodeScanningActivity
import com.isar.imagine.barcode_scenning.models.BillingDataModel
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.databinding.FragmentSecondBinding

class BillingPanelFragment : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var binding: FragmentSecondBinding


    private  var listData: MutableList<BillingDataModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSecondBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dataList = intent.getSerializableExtra("data") as List<BillingDataModel>
        listData.addAll(dataList)

        listViewInitialize(view = binding.root)

        addMoreData()

        saveData()


    }

    private fun saveData() {
        binding.loginButton.setOnClickListener {

        }
    }

    private fun addMoreData(){
        binding.buttonAdd.setOnClickListener{
            startActivity(
                Intent(
                    this@BillingPanelFragment,
                    BarCodeScanningActivity::class.java
                ).apply {
                    putExtra("dataList",listData.toCollection(ArrayList()))
                    putExtra("isBilling",true)
                }
            )
        }
    }

    private fun listViewInitialize(view: View) {
        expandableListView = view.findViewById(R.id.listViewBilling)

        val headerMap = listData.map { it.brand }.distinct()
        val itemMap = listData.map { it.toInventory() }.distinct().groupBy { it.brand }
        val expandableListAdapter =
            InventoryExpandableListAdapter(applicationContext, headerMap, itemMap)
        expandableListView.setAdapter(expandableListAdapter)


    }

    fun BillingDataModel.toInventory(): InventoryItem {
        return InventoryItem(
            brand = this.brand,
            model = this.model,
            variant = this.variant,
            condition = this.condition,
            purchasePrice = this.purchasePrice,
            sellingPrice = this.sellingPrice,
            quantity = this.quantity.toInt(),
            notes = this.notes
        )
    }

}
