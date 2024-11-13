package com.isar.imagine.barcode

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.isar.imagine.R
import com.isar.imagine.data.model.BarcodeField


/** Presents a list of field info in the detected barcode.  */
internal class BarcodeFieldAdapter(
    private val barcodeFieldList: List<BarcodeField>,
) : RecyclerView.Adapter<BarcodeFieldAdapter.BarcodeFieldViewHolder>() {

    internal class BarcodeFieldViewHolder private constructor(view: View) :
        RecyclerView.ViewHolder(view) {

        private val labelView: TextView = view.findViewById(R.id.barcode_field_label)
        private val valueView: EditText = view.findViewById(R.id.barcode_field_value)

        fun bindBarcodeField(barcodeField: BarcodeField) {

            labelView.text = barcodeField.label
            valueView.hint = barcodeField.value


            valueView.apply {
                isFocusable = barcodeField.isEditable
                isClickable = barcodeField.isEditable
                isCursorVisible = barcodeField.isEditable
            }
            try {
                valueView.addTextChangedListener (object :TextWatcher{
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {

                    }


                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val currentValue = valueView.text.toString().toIntOrNull() ?: 0
                        val maxValue = barcodeField.value.toIntOrNull() ?: Int.MAX_VALUE

                        if (currentValue > maxValue) {
                            valueView.error = "Maximum is $maxValue"
                        } else {
                            valueView.error = null
                        }

                    }

                    override fun afterTextChanged(s: Editable?) {

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
        BarcodeFieldViewHolder.create(parent,)

    override fun onBindViewHolder(holder: BarcodeFieldViewHolder, position: Int) =
        holder.bindBarcodeField(barcodeFieldList[position])

    override fun getItemCount(): Int = barcodeFieldList.size
}