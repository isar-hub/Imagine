package com.isar.imagine.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.Fragments.Transaction
import com.isar.imagine.R

class TransactionAdapter(private val transactionList: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewBrand: TextView = itemView.findViewById(R.id.textview_brand)
        val textViewModel: TextView = itemView.findViewById(R.id.textview_model)
        val textViewVariant: TextView = itemView.findViewById(R.id.textview_variant)
        val textViewColor: TextView = itemView.findViewById(R.id.textview_color)
        val textViewQuantity: TextView = itemView.findViewById(R.id.textview_quantity)
        val textViewPrice: TextView = itemView.findViewById(R.id.textview_price)
        val textViewTotalPrice: TextView = itemView.findViewById(R.id.textview_total_price)
        val textViewDate: TextView = itemView.findViewById(R.id.textview_date)
        val textViewCondition: TextView = itemView.findViewById(R.id.textview_condition)
        val textViewDescription: TextView = itemView.findViewById(R.id.textview_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.textViewBrand.text = transaction.brand
        holder.textViewModel.text = transaction.model
        holder.textViewVariant.text = transaction.variant
        holder.textViewColor.text = transaction.color
        holder.textViewQuantity.text = "Quantity: ${transaction.quantity}"
        holder.textViewPrice.text = "Price: ₹${transaction.price}"
        holder.textViewTotalPrice.text = "Total: ₹${transaction.totalPrice}"
        holder.textViewDate.text = transaction.date
        holder.textViewCondition.text = "Condition: ${transaction.condition}"
        holder.textViewDescription.text = "Description: ${transaction.description}"
    }

    override fun getItemCount(): Int = transactionList.size
}
