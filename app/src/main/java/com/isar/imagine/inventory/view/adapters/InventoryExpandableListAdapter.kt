package com.isar.imagine.inventory.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.isar.imagine.R
import com.isar.imagine.billing.InventoryItem


class InventoryExpandableListAdapter(
    private val context: Context,
    private val expandableListTitle: List<String>, // List of group titles
    private val expandableListDetail: Map<String, List<InventoryItem>>,// Map of group to children,
    private val actionUnit: (Int,Int) -> Unit
) : BaseExpandableListAdapter() {
    companion object {
        private const val TAG = "InventoryAdapter"
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any {
        Log.e(
            TAG,
            "getChild: Getting child at position $expandedListPosition for group $listPosition"
        )

        return expandableListDetail[expandableListTitle[listPosition]]!![expandedListPosition]
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        Log.e(TAG, "getChildId: Child ID at position $expandedListPosition for group $listPosition")

        return expandedListPosition.toLong()
    }

    override fun getChildrenCount(listPosition: Int): Int {
        val key = expandableListTitle[listPosition]

        return expandableListDetail[key]?.size ?: 0
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
        listPosition: Int,
        expandedListPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        Log.e(
            TAG,
            "getChildView: Creating view for child at position $expandedListPosition in group $listPosition"
        )

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.expandable_list_item, parent, false)
        val item = getChild(listPosition, expandedListPosition) as InventoryItem
        Log.e(TAG, "item of expanded $item")


        with(view) {
            findViewById<TextView>(R.id.model).text = "Model: ${item.model}"
            findViewById<TextView>(R.id.variant).text = "Variant: ${item.variant}"
            findViewById<TextView>(R.id.condition).text = "Condition: ${item.condition}"
            findViewById<TextView>(R.id.imei1).text = "IMEI 1: ${item.imei1}"
            findViewById<TextView>(R.id.imei2).text = "IMEI 2: ${item.imei2}"
            findViewById<TextView>(R.id.purchasePrice).text =
                "Purchase Price: ${item.purchasePrice}"
            findViewById<TextView>(R.id.sellingPrice).text = "Selling Price: ${item.sellingPrice}"
            findViewById<TextView>(R.id.quantity).text = "Quantity: ${item.quantity}"
            findViewById<TextView>(R.id.notes).text = "Notes: ${item.notes}"
            findViewById<ImageButton>(R.id.buttonDelete).setOnClickListener {
                actionUnit(listPosition,expandedListPosition)
            }
        }

        return view
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?
    ): View {
        Log.e(
            TAG,
            "getGroupView: Creating view for group at position $listPosition, isExpanded: $isExpanded"
        )

        // Use the provided Android layout for a simple expandable list item
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.expandable_list_header, parent, false)

        val listTitleTextView = view.findViewById<TextView>(R.id.text1)
        val listTitle = getGroup(listPosition) as String
        listTitleTextView.text = listTitle

        return view
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {

        Log.e(
            TAG,
            "isChildSelectable: Checking if child at position $expandedListPosition in group $listPosition is selectable"
        )
        return true

    }
}
