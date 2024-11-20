package com.isar.imagine.billing

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.isar.imagine.Adapters.InventoryExpandableListAdapter
import com.isar.imagine.R
import com.isar.imagine.barcode_scenning.BarCodeScanningActivity
import com.isar.imagine.barcode_scenning.models.BillingDataModel
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.databinding.FragmentSecondBinding
import com.isar.imagine.responses.UserDetails
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Invoice.createPdf
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView
import com.isar.imagine.viewmodels.AppDatabase
import com.isar.imagine.viewmodels.RetailerEntity
import com.isar.imagine.viewmodels.RetailerRepository
import com.isar.imagine.viewmodels.UserDatabase
import kotlinx.coroutines.launch

class BillingPanelFragment : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var binding: FragmentSecondBinding
    private val functions = Firebase.functions

    private val repository: RetailerRepository by lazy {
        RetailerRepository(
            FirebaseAuth.getInstance(), UserDatabase.getDatabase(this).userDao()
        )
    }
    private val billingRepository: BillingRepository by lazy {
        BillingRepository(
            repository, Firebase.functions, FirebaseFirestore.getInstance()

        )

    }
    private val viewModel: BillingViewModel by viewModels {
        BillingViewmodelFactory(billingRepository)
    }


    private var listData: MutableList<BillingDataModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSecondBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val dataList = intent.getSerializableExtra("data") as List<BillingDataModel>
        CommonMethods.showLogs("BILLING", "get size is ${dataList[0].quantity}")
        listData.addAll(dataList)

        listViewInitialize(view = binding.root)

        addMoreData()

        saveData()


        viewModel.retailers.observe(this) {
            when (it) {
                is Results.Error -> CustomDialog.showAlertDialog(
                    this, getTextView(it.message!!), "Error"
                )

                is Results.Loading -> CustomProgressBar.show(this, "Loading Retailers")
                is Results.Success -> {
                    if (!it.data.isNullOrEmpty()) {
                        populateBrandSpinner(it.data)
                    }
                    CustomProgressBar.dismiss()
                    CommonMethods.showLogs("BILLING", "result ${it.data}")
                }
            }
        }


    }

    private fun populateBrandSpinner(brands: List<UserDetails>) {

        val list = brands.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.autoCompleteTextView.setAdapter(adapter)

        binding.autoCompleteTextView.setOnFocusChangeListener { _, _ ->
            binding.autoCompleteTextView.showDropDown()
        }
        binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            retailerEntity = brands[position]
        }

    }

    lateinit var retailerEntity: UserDetails

    private fun saveData() {
        binding.loginButton.setOnClickListener {
            binding.loginButton.isEnabled = false
            CustomProgressBar.show(this, "Loading...")


            lifecycleScope.launch {
                viewModel.addTransaction(listData, retailerEntity.uid) {
                    if (it) {
                        generateInvoice()
                    } else {
                        CommonMethods.showLogs("BILLING", "ERROR IN BILLING")
                    }
                }
            }
        }
    }


    private fun generateInvoice() {
        val (user, productInformationList) = viewModel.generateInvoiceData(retailerEntity, listData)
        CommonMethods.showLogs("BILLING", "List ${productInformationList.size}")
        CustomProgressBar.dismiss()
        val path = createPdf(user, productInformationList, productInformationList.size)
        openShowBill(path)
    }

    private fun openShowBill(path: String) {
        val bundle = bundleOf("PDF_PATH" to path)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<ShowBillFragment>(R.id.billingMain, args = bundle)
        }
    }


    private fun addMoreData() {
        binding.buttonAdd.setOnClickListener {
            startActivity(Intent(
                this@BillingPanelFragment, BarCodeScanningActivity::class.java
            ).apply {
                putExtra("dataList", listData.toCollection(ArrayList()))
                putExtra("isBilling", true)
            })
        }
    }

    private fun listViewInitialize(view: View) {
        expandableListView = view.findViewById(R.id.listViewBilling)

        val headerMap = listData.map { it.brand }.distinct()
        val itemMap = listData.map { it.toInventory() }.distinct().groupBy { it.brand }
        CommonMethods.showLogs("TAG", "ITEM MAP IS $itemMap")


        val expandableListAdapter = InventoryExpandableListAdapter(this, headerMap, itemMap)
        expandableListView.setAdapter(expandableListAdapter)
        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            CommonMethods.showLogs("TAG","CLICKED $groupPosition  $childPosition $id")

            expandableListView.expandGroup(groupPosition)


        }
        expandableListView.setOnGroupExpandListener { groupPosition ->
            CommonMethods.showLogs("TAG", "Group expanded at position: $groupPosition")
        }


    }

    fun BillingDataModel.toInventory(): InventoryItem {
        return InventoryItem(
            quantity = this.quantity.toInt(),
            brand = this.brand,
            model = this.model,
            variant = this.variant,
            condition = this.condition,
            purchasePrice = this.purchasePrice,
            sellingPrice = this.sellingPrice,
            notes = this.notes
        )
    }
}
