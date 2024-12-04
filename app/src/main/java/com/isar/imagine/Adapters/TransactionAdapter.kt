package com.isar.imagine.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.R
import com.isar.imagine.billing.Transactions
import com.isar.imagine.billing.TransactionsResponse
import org.w3c.dom.Text

class TransactionAdapter(private val transactionList: List<TransactionsResponse>,private  val ctx: Context) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewQuantity: TextView = itemView.findViewById(R.id.textview_quantity)
        val textViewTotalPrice: TextView = itemView.findViewById(R.id.textview_total_price)
        val textViewDate: TextView = itemView.findViewById(R.id.textview_date)
        val textViewRetailer: TextView = itemView.findViewById(R.id.textview_retailer)
        val transactionId : TextView = itemView.findViewById(R.id.transactionId)
        val productList : ListView = itemView.findViewById(R.id.productIdList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        val productIdAdapter = ArrayAdapter(ctx,android.R.layout.simple_spinner_dropdown_item,transaction.productId)
        holder.textViewQuantity.text = transaction.totalQuantity.toString()
        holder.textViewTotalPrice.text = transaction.billValue.toString()
        holder.textViewDate.text = transaction.createdAt
        holder.textViewRetailer.text = transaction.retailerId
        holder.transactionId.text = transaction.transactionID
        holder.productList.adapter = productIdAdapter

    }

    override fun getItemCount(): Int = transactionList.size
}
