package com.isar.imagine.barcode.adapters

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.R
import com.isar.imagine.barcode.data.BarcodeField
import com.isar.imagine.utils.CommonMethods


/** Presents a list of field info in the detected barcode.  */
internal class BarcodeFieldAdapter(
    private val barcodeFieldList: List<BarcodeField>,
    private val onQuantityChanged: (Boolean, Int, Int) -> Unit
) : RecyclerView.Adapter<BarcodeFieldAdapter.BarcodeFieldViewHolder>() {

    internal class BarcodeFieldViewHolder private constructor(view: View) :
        RecyclerView.ViewHolder(view) {

        private val labelView: TextView = view.findViewById(R.id.barcode_field_label)
        private val valueView: EditText = view.findViewById(R.id.barcode_field_value)

        fun bindBarcodeField(
            barcodeField: BarcodeField,
            onQuantityChanged: (Boolean, Int) -> Unit
        ) {

            labelView.text = barcodeField.label
            valueView.hint = barcodeField.value


            valueView.apply {
                isFocusable = barcodeField.isEditable
                isClickable = barcodeField.isEditable
                isCursorVisible = barcodeField.isEditable
                if (barcodeField.isEditable) requestFocus()
                valueView.inputType = InputType.TYPE_CLASS_NUMBER
            }
            val quantity = valueView.text.toString().toIntOrNull() ?: 0
            CommonMethods.showLogs("BILLING", "Quantity $quantity")
            if (quantity <= 0 || quantity > barcodeField.value.toString().toInt()) {
                onQuantityChanged(false, quantity)
            } else {
                onQuantityChanged(true, quantity)
            }

            try {
                valueView.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?, start: Int, count: Int, after: Int
                    ) {
                        if (barcodeField.isEditable && valueView.text.toString().isEmpty()) {
                            valueView.error = "Please Add Quantity"
                        }
                    }

                    override fun onTextChanged(
                        s: CharSequence?, start: Int, before: Int, count: Int
                    ) {
                        val currentValue = valueView.text.toString().toIntOrNull() ?: 0
                        val maxValue = barcodeField.value.toIntOrNull() ?: Int.MAX_VALUE
                        if (currentValue > maxValue) {
                            valueView.error = "Maximum is $maxValue"
                        } else if (barcodeField.isEditable && currentValue <= 0) {
                            valueView.error = "Please Add Quantity"
                            onQuantityChanged(false, currentValue)

                        } else {
                            onQuantityChanged(true, currentValue)
                            valueView.error = null
                        }

                    }

                    override fun afterTextChanged(s: Editable?) {
                        val current = s?.toString()?.toIntOrNull() ?: 0
                        if ((current) <= 0 || current > (barcodeField.value.toIntOrNull()
                                ?: Int.MAX_VALUE)
                        ) {
                            onQuantityChanged(false, current)

                        } else {
                            onQuantityChanged(true, current)
                        }
                    }


                })

            } catch (e: NumberFormatException) {
                valueView.error = null
            }


        }


        companion object {

            fun create(parent: ViewGroup): BarcodeFieldViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.barcode_field, parent, false)
                return BarcodeFieldViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeFieldViewHolder =
        BarcodeFieldViewHolder.create(parent)

    override fun onBindViewHolder(holder: BarcodeFieldViewHolder, position: Int) {
        holder.bindBarcodeField(barcodeFieldList[position]) { isOk, quantity ->
            CommonMethods.showLogs("BILLING", "ON QUANTITY changed called")
            onQuantityChanged(isOk, position, quantity)
        }
    }

    override fun getItemCount(): Int = barcodeFieldList.size
}