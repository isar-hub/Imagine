package com.isar.imagine.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.isar.imagine.R
import com.isar.imagine.data.model.InventoryItem

class InventoryExpandableListAdapter(
    private val context: Context,
    private val expandableListTitle: List<String>, // List of group titles
    private val expandableListDetail: Map<String, List<InventoryItem>> // Map of group to children
) : BaseExpandableListAdapter() {
    companion object {
        private const val TAG = "InventoryAdapter"
    }
    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        Log.e(TAG, "getChild: Getting child at position $expandedListPosition for group $listPosition")

        return expandableListDetail[expandableListTitle[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        Log.e(TAG, "getChildId: Child ID at position $expandedListPosition for group $listPosition")

        return expandedListPosition.toLong()
    }

    override fun getChildrenCount(listPosition: Int): Int {
        val count = expandableListDetail[expandableListTitle[listPosition]]!!.size

        Log.e(TAG, "getChildrenCount: Number of children for group $listPosition is $count")

        return count
    }

    override fun getGroup(listPosition: Int): Any {
        Log.e(TAG, "getGroup: Getting group at position $listPosition")

        return expandableListTitle[listPosition]
    }

    override fun getGroupCount(): Int {
        val count = expandableListTitle.size
        Log.e(TAG, "getGroupCount: Number of groups is $count")
        return count
    }

    override fun getGroupId(listPosition: Int): Long {
        Log.e(TAG, "getGroupId: Group ID at position $listPosition")
        return listPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        Log.e(TAG, "hasStableIds: Checking if IDs are stable")
        return false
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int, isLastChild: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View {
        Log.e(TAG, "getChildView: Creating view for child at position $expandedListPosition in group $listPosition")

        var view = convertView
        if (view == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.expandable_list_item, null)
        }
        val item = getChild(listPosition, expandedListPosition) as InventoryItem
        val modelTextView = view!!.findViewById<TextView>(R.id.model)
        val variantTextView = view.findViewById<TextView>(R.id.variant)
        val conditionTextView = view.findViewById<TextView>(R.id.condition)
        val purchasePriceTextView = view.findViewById<TextView>(R.id.purchasePrice)
        val sellingPriceTextView = view.findViewById<TextView>(R.id.sellingPrice)
        val quantityTextView = view.findViewById<TextView>(R.id.quantity)
        val notesTextView = view.findViewById<TextView>(R.id.notes)

        "Model: ${item.model}".also { modelTextView.text = it }
        "Variant: ${item.variant}".also { variantTextView.text = it }
        "Condition: ${item.condition}".also { conditionTextView.text = it }
        "Purchase Price: ${item.purchasePrice}".also { purchasePriceTextView.text = it }
        "Selling Price: ${item.sellingPrice}".also { sellingPriceTextView.text = it }
        "Quantity: ${item.quantity}".also { quantityTextView.text = it }
        "Notes: ${item.notes}".also { notesTextView.text =it }

        return view
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup?
    ): View? {
        Log.e(TAG, "getGroupView: Creating view for group at position $listPosition, isExpanded: $isExpanded")

        var view = convertView
        if (view == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.expandable_list_header, null)
        }
        val listTitle = getGroup(listPosition) as String
        val listTitleTextView = view?.findViewById<TextView>(R.id.header)
        if (listTitleTextView != null) {
            listTitleTextView.text = listTitle
        }
        return view
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        Log.e(TAG, "isChildSelectable: Checking if child at position $expandedListPosition in group $listPosition is selectable")

        return true
    }
}
