package com.isar.imagine.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.Adapters.RetailerAdapter
import com.isar.imagine.R
import com.isar.imagine.data.Retailer



class RetailerFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvWarrantyPhones: TextView
    private lateinit var tvTotalItems: TextView
    private lateinit var searchView: SearchView
    private lateinit var adapter: RetailerAdapter
    private val retailerList = mutableListOf<Retailer>()
    private lateinit var btnAddRetailer : Button
    lateinit var etRetailerName : EditText



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_retailer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rvRetailers)
        tvWarrantyPhones = view.findViewById(R.id.tvWarrantyPhones)
        tvTotalItems = view.findViewById(R.id.tvTotalItems)
        searchView = view.findViewById(R.id.svRetailerSearch)
        btnAddRetailer = view.findViewById(R.id.btnAddRetailer)
        btnAddRetailer.setOnClickListener{
            if (etRetailerName.text.isNotEmpty()){
                retailerList.add(Retailer(etRetailerName.text.toString(),0,0))
                adapter.notifyDataSetChanged()
            }
        }
        setupRecyclerView()
        setupSearchView()
        loadDummyData()
    }

    private fun setupRecyclerView() {
         adapter = RetailerAdapter(retailerList) { retailer ->
            // Update retailer details
            tvWarrantyPhones.text = "Total Phones in Warranty: ${retailer.warrantyPhones}"
            tvTotalItems.text = "Total Items Given to Retailer: ${retailer.totalItems}"
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Optional: handle submit action if needed
                Log.e("RetailerFragment", "Search query submitted: $query")

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.e("RetailerFragment", "Search query text changed: $newText")

                adapter.filter.filter(newText)
                return true
            }
        })
    }
    private fun loadDummyData() {
        retailerList.add(Retailer("Retailer A2", 10, 50))
        retailerList.add(Retailer("Retailer B1", 5, 20))
        retailerList.add(Retailer("Retailer C1", 8, 30))
        retailerList.add(Retailer("Retailer A3", 10, 50))
        retailerList.add(Retailer("Retailer B1", 5, 20))
        retailerList.add(Retailer("Retailer C2", 8, 30))
        retailerList.add(Retailer("Retailer A3", 10, 50))
        retailerList.add(Retailer("Retailer B5", 5, 20))
        retailerList.add(Retailer("Retailer C5", 8, 30))
        retailerList.add(Retailer("Retailer A45", 10, 50))
        retailerList.add(Retailer("Retailer B45", 5, 20))
        retailerList.add(Retailer("Retailer C45", 8, 30))
        retailerList.add(Retailer("Retailer A34", 10, 50))
        retailerList.add(Retailer("Retailer B33", 5, 20))
        retailerList.add(Retailer("Retailer C23", 8, 30))
        retailerList.add(Retailer("Retailer A1", 10, 50))
        retailerList.add(Retailer("Retailer B24", 5, 20))
        retailerList.add(Retailer("Retailer C5", 8, 30))
        retailerList.add(Retailer("Retailer A7", 10, 50))
        retailerList.add(Retailer("Retailer B7", 5, 20))
        retailerList.add(Retailer("Retailer C8", 8, 30))
        retailerList.add(Retailer("Retailer A9", 10, 50))
        retailerList.add(Retailer("Retailer B9", 5, 20))
        retailerList.add(Retailer("Retailer C9", 8, 30))
        retailerList.add(Retailer("Retailer A0", 10, 50))
        retailerList.add(Retailer("Retailer B0", 5, 20))
        retailerList.add(Retailer("Retailer C0", 8, 30))
        retailerList.add(Retailer("Retailer A123", 10, 50))
        retailerList.add(Retailer("Retailer B124", 5, 20))
        retailerList.add(Retailer("Retailer C123", 8, 30))
        retailerList.add(Retailer("Retailer A1234", 10, 50))
        retailerList.add(Retailer("Retailer B144", 5, 20))
        retailerList.add(Retailer("Retailer C53", 8, 30))
        retailerList.add(Retailer("Retailer A1245", 10, 50))
        retailerList.add(Retailer("Retailer B54", 5, 20))
        retailerList.add(Retailer("Retailer C577", 8, 30))
        retailerList.add(Retailer("Retailer A68", 10, 50))
        retailerList.add(Retailer("Retailer B85", 5, 20))
        retailerList.add(Retailer("Retailer C85", 8, 30))
        retailerList.add(Retailer("Retailer A84", 10, 50))
        retailerList.add(Retailer("Retailer B824", 5, 20))
        retailerList.add(Retailer("Retailer C8433", 8, 30))
        retailerList.add(Retailer("Retailer A3435", 10, 50))
        retailerList.add(Retailer("Retailer B2782", 5, 20))
        retailerList.add(Retailer("Retailer C57", 8, 30))
        retailerList.add(Retailer("Retailer Ag", 10, 50))
        retailerList.add(Retailer("Retailer B35", 5, 20))
        retailerList.add(Retailer("Retailer C34f", 8, 30))

        adapter.retailersFullList = retailerList
        adapter.notifyDataSetChanged()
    }
}