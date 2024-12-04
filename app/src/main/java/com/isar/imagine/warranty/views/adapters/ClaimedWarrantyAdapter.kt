package com.isar.imagine.warranty.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.R
import com.isar.imagine.databinding.WarrantyItemBinding
import com.isar.imagine.warranty.views.ProductView
import com.isar.imagine.warranty.data.ClaimedModel
import com.isar.imagine.warranty.data.WarrantyStatus

class ClaimedWarrantyAdapter(
    var mContext: Context, var claimedWarranty: List<ClaimedModel>
) : RecyclerView.Adapter<ClaimedWarrantyAdapter.MyViewHolder>() {

    class MyViewHolder(var view: WarrantyItemBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = WarrantyItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val claimed = claimedWarranty[position]
        val view = holder.view

        view.txtReason.text = claimed.reason
        view.txtDescription.text = claimed.reasonDescription
        view.txtDate.text = claimed.createdAt
        view.txtStatus.text = claimed.status.name
        view.txtStatus.setTextColor(getColor(claimed.status))
        view.viewWarranty.setOnClickListener {
            // Create a new instance of the fragment and pass the serial number
            val fragment = ProductView.newInstance(claimed.serialNumber)
            Navigation.findNavController(view.root)
                .navigate(R.id.productViewFragment, args = fragment)
        }

    }

    private fun getColor(status: WarrantyStatus): Int {
        return when (status) {
            WarrantyStatus.CLAIMED -> mContext.resources.getColor(android.R.color.holo_blue_dark)
            WarrantyStatus.PROCESSED -> mContext.resources.getColor(android.R.color.holo_orange_dark)
            WarrantyStatus.COMPLETED -> mContext.resources.getColor(android.R.color.holo_green_dark)
        }
    }

    override fun getItemCount(): Int {
        return claimedWarranty.size
    }
}