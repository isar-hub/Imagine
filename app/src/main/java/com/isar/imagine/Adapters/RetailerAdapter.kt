package com.isar.imagine.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.R
import com.isar.imagine.data.Retailer

class RetailerAdapter(
    private var retailers: List<Retailer>,
    private val listener: (Retailer) -> Unit
) : RecyclerView.Adapter<RetailerAdapter.ViewHolder>(), Filterable {

     var retailersFullList: List<Retailer> = ArrayList(retailers)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val retailerName: TextView = itemView.findViewById(R.id.tvRetailerName)

        fun bind(retailer: Retailer) {
            retailerName.text = retailer.name
            itemView.setOnClickListener { listener(retailer) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_retailer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Add logging to confirm the data in retailersFullList
        Log.e("RetailerAdapter", "Full list data: ${retailersFullList.map { it.name }}")

        holder.bind(retailers[position])
    }

    override fun getItemCount(): Int = retailers.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                Log.e("Retaile  rAdapter", "Performing filtering with constraint: $constraint")

                val filterPattern = constraint?.toString()?.lowercase()?.trim() ?: ""
                Log.e("RetailerAdapter", "Filter pattern: $filterPattern")

                val filteredList = if (filterPattern.isEmpty()) {
                    retailersFullList
                } else {
                    retailersFullList.filter {
                        it.name.lowercase().contains(filterPattern)
                    }
                }

                Log.e("RetailerAdapter", "Filtered list size: ${filteredList.size}")
                return FilterResults().apply { values = filteredList }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                Log.d("RetailerAdapter", "Publishing results with count: ${results?.values?.let { (it as List<*>).size }}")
                retailers = results?.values as List<Retailer>
                notifyDataSetChanged()
            }
        }
    }




}
