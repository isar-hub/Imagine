package com.isar.imagine.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.R
import com.isar.imagine.responses.UserDetails

//import com.isar.imagine.data.Retailer
data class Retailer(val name: String)
class RetailerAdapter(
    private var retailers: List<UserDetails>, private val listener: (UserDetails) -> Unit
) : RecyclerView.Adapter<RetailerAdapter.ViewHolder>(), Filterable {

    var retailersFullList: List<UserDetails> = ArrayList(retailers)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val retailerName: TextView = itemView.findViewById(R.id.tvRetailerName)
        private val role: TextView = itemView.findViewById(R.id.role)
        private val company: TextView = itemView.findViewById(R.id.company)
        private val state: TextView = itemView.findViewById(R.id.state)
        private val viewBtn: Button = itemView.findViewById(R.id.viewBtn)


        fun bind(retailer: UserDetails) {
            retailerName.text = retailer.name
            role.text = retailer.role.displayName
            company.text = retailer.companyName
            state.text = retailer.state
            itemView.setOnClickListener { listener(retailer) }
            viewBtn.setOnClickListener { listener(retailer) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_retailer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Add logging to confirm the data in retailersFullList
        Log.e("RetailerAdapter", "Full list data: ${retailersFullList.map { it }}")

        holder.bind(retailers[position])
    }
    fun isEmpty() : Boolean{
        return retailers.isEmpty()
    }

    override fun getItemCount(): Int = retailers.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterPattern = constraint?.toString()?.lowercase()?.trim() ?: ""

                // If no constraint, return the full list
                val filteredList = if (filterPattern.isEmpty()) {
                    retailersFullList
                } else {
                    retailersFullList.filter { retailer ->
                        // Check across multiple fields
                        retailer.name.lowercase()
                            .contains(filterPattern) || retailer.role.displayName.lowercase()
                            .contains(filterPattern) || retailer.companyName?.lowercase()
                            ?.contains(filterPattern) == true || retailer.state.lowercase()
                            .contains(filterPattern) || retailer.email.lowercase()
                            .contains(filterPattern) || retailer.mobile.lowercase()
                            .contains(filterPattern)
                    }
                }

                return FilterResults().apply { values = filteredList }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                retailers = results?.values as? List<UserDetails> ?: emptyList()
                notifyDataSetChanged()
            }
        }
    }


}
