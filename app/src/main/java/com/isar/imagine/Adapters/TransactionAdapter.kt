package com.isar.imagine.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.transactions.Transaction
import com.isar.imagine.R
import org.w3c.dom.Text

class TransactionAdapter(private val transactionList: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewQuantity: TextView = itemView.findViewById(R.id.textview_quantity)
        val textViewTotalPrice: TextView = itemView.findViewById(R.id.textview_total_price)
        val textViewDate: TextView = itemView.findViewById(R.id.textview_date)
        val textViewRetailer: TextView = itemView.findViewById(R.id.textview_retailer)
        val transactionId : TextView = itemView.findViewById(R.id.transactionId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]


        holder.textViewQuantity.text = transaction.quantity.toString()
        holder.textViewTotalPrice.text = transaction.totalPrice.toString()
        holder.textViewDate.text = transaction.date
        holder.textViewRetailer.text = transaction.retailer
        holder.transactionId.text = transaction.id
    }

    override fun getItemCount(): Int = transactionList.size
}
