package com.isar.imagine.barcode

import WorkflowModel
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.isar.imagine.billing.BillingPanelFragment
import com.isar.imagine.R
import com.isar.imagine.barcode_scenning.models.BillingDataModel
import com.isar.imagine.data.model.BarcodeField
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.CustomDialog

/** Displays the bottom sheet to present barcode fields contained in the detected barcode.  */
class BarcodeResultFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        layoutInflater: LayoutInflater, viewGroup: ViewGroup?, bundle: Bundle?
    ): View {
        val view = layoutInflater.inflate(R.layout.barcode_bottom_sheet, viewGroup)
        val btn: MaterialButton = view.findViewById(R.id.btnConfirm)


        btn.setOnClickListener {
            val textView = EditText(requireContext()).apply {
                hint = getQuantity().toString()
                width = MATCH_PARENT
                inputType = InputType.TYPE_CLASS_NUMBER

            }
            if (getQuantity() > 0) {
                CustomDialog.showAlertDialog(requireContext(),
                    textView,
                    "Please Enter Quantity max is : ${getQuantity()}",
                    {
                        if (Integer.parseInt(textView.text.toString()) <= getQuantity()) {

                            startActivity(Intent(
                                requireContext(), BillingPanelFragment::class.java
                            ).apply {
                                val item = getItem()
                                CommonMethods.showLogs("BILLING","get item size ${item.size}")
                                putExtra("data", getItem())
                            })
                            activity?.finish()
                        }
                    })
            }
        }

        val arguments = arguments
        val barcodeFieldList: ArrayList<BarcodeField> =
            if (arguments?.containsKey(ARG_BARCODE_FIELD_LIST) == true) {
                arguments.getParcelableArrayList(ARG_BARCODE_FIELD_LIST) ?: ArrayList()

            } else {
                Log.e(TAG, "No barcode field list passed in!")
                ArrayList()
            }

        Log.e("Test", barcodeFieldList.toString())
        view.findViewById<RecyclerView>(R.id.barcode_field_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = BarcodeFieldAdapter(barcodeFieldList)
        }

        return view
    }

    override fun onDismiss(dialogInterface: DialogInterface) {


        activity?.let {
            // Back to working state after the bottom sheet is dismissed.
            onResume()
            ViewModelProvider(this)[WorkflowModel::class.java].setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
        }
        super.onDismiss(dialogInterface)
    }

    companion object {

        private const val TAG = "BarcodeResultFragment"
        private const val ARG_BARCODE_FIELD_LIST = "arg_barcode_field_list"
        private var QUANTITY = 0L
        private var item: MutableList<BillingDataModel> = mutableListOf()

        fun getQuantity(): Long {
            return QUANTITY
        }

        fun setQuantity(quantity: Long) {
            QUANTITY = quantity
        }

        fun getItem(): ArrayList<BillingDataModel>{
            return this.item as ArrayList
        }

        fun setItem(item: List<BillingDataModel>) {
            CommonMethods.showLogs("BILLING","Size of list data in set ${item.size}")
            this.item.clear()
            this.item.addAll(item)
        }

        fun show(fragmentManager: FragmentManager, barcodeFieldArrayList: ArrayList<BarcodeField>) {
            val barcodeResultFragment = BarcodeResultFragment()
            barcodeResultFragment.arguments = Bundle().apply {
                putParcelableArrayList(ARG_BARCODE_FIELD_LIST, barcodeFieldArrayList)
            }

            barcodeResultFragment.show(fragmentManager, TAG)
        }

        fun dismiss(fragmentManager: FragmentManager) {
            (fragmentManager.findFragmentByTag(TAG) as BarcodeResultFragment?)?.dismiss()
        }
    }
}