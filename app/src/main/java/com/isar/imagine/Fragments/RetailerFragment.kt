package com.isar.imagine.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.Adapters.RetailerAdapter
import com.isar.imagine.R
import com.isar.imagine.data.Retailer
import com.isar.imagine.databinding.FragmentInventory2Binding
import com.isar.imagine.databinding.FragmentRetailerBinding
import com.isar.imagine.inventory.InventoryRepositoryImpl
import com.isar.imagine.inventory.InventoryViewModel
import com.isar.imagine.inventory.MobileViewModelFactory
import com.isar.imagine.utils.Results
import com.isar.imagine.viewmodels.RetailerFragmentViewHolder


class RetailerFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var adapter: RetailerAdapter
    private val retailerList = mutableListOf<Retailer>()
    private lateinit var binding: FragmentRetailerBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModel: RetailerFragmentViewHolder by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState:      Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRetailerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        observers()








//        recyclerView = view.findViewById(R.id.rvRetailers)
//        searchView = view.findViewById(R.id.svRetailerSearch)
//        btnAddRetailer = view.findViewById(R.id.btnAddRetailer)
        binding.btnAddRetailer.setOnClickListener{

            showCreateUserDialog()
        }
        setupRecyclerView()
//        setupSearchView()
        loadDummyData()
    }

    private fun observers() {
        viewModel.userCreated.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> {
//                    val progressDialog = ProgressDialog(context)
//                    progressDialog.setMessage("User creating ...")
//                    progressDialog.show()
                    binding.loader.visibility = View.VISIBLE

                }

                is Results.Success -> {
                    binding.loader.visibility = View.GONE
                    val name = result.data?.displayName
                    if (name != null)
                    retailerList.add(Retailer(name = name, warrantyPhones = 10, totalItems = 10))

                    Toast.makeText(
                        requireContext(),
                        "User created: ${result.data?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is Results.Error -> {
                    binding.loader.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun showCreateUserDialog() {
        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Inflate the custom layout
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_user, null)

        // Create the AlertDialog
        val dialog = context?.let {
            AlertDialog.Builder(it)
                .setView(dialogView)
                .setCancelable(true) // Allow dismissing by tapping outside
                .create()
        }

        // Get references to the views in the dialog layout
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_email)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.edit_password)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name)
        val createButton = dialogView.findViewById<Button>(R.id.submit)

        // Handle Create User button click
        createButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val name = passwordEditText.text.toString().trim()

            if (email.isEmpty()){
                emailEditText.error = "user id  cannot be empty"
            }
            else if(password.isEmpty() && password.length < 6  ){
                passwordEditText.error = "password cannot be empty"
            }
            else if (name.isEmpty()){
                nameEditText.error = "name cannot be empty"
            }
            else {
                if (dialog != null) {
                    dialog.hide()
                    viewModel.createUser(email,password,name)
                }
            }
        }

        // Show the dialog
        dialog?.show()
    }
//
//    private fun createUserWithFirebase(email: String, password: String, dialog: AlertDialog) {
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show()
//                    dialog.dismiss() // Close the dialog on success
//                } else {
//                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun setupRecyclerView() {
         adapter = RetailerAdapter(retailerList) { retailer ->
            // Update retailer details

        }
        binding.rvRetailers.layoutManager = LinearLayoutManager(context)
        binding.rvRetailers.adapter = adapter
    }
//    private fun setupSearchView() {
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                // Optional: handle submit action if needed
//                Log.e("RetailerFragment", "Search query submitted: $query")
//
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                Log.e("RetailerFragment", "Search query text changed: $newText")
//
//                adapter.filter.filter(newText)
//                return true
//            }
//        })
//    }
    private fun loadDummyData() {
//        retailerList.add(Retailer("Retailer A2", 10, 50))
//        retailerList.add(Retailer("Retailer B1", 5, 20))
//        retailerList.add(Retailer("Retailer C1", 8, 30))
//        retailerList.add(Retailer("Retailer A3", 10, 50))
//        retailerList.add(Retailer("Retailer B1", 5, 20))
//        retailerList.add(Retailer("Retailer C2", 8, 30))
//        retailerList.add(Retailer("Retailer A3", 10, 50))
//        retailerList.add(Retailer("Retailer B5", 5, 20))
//        retailerList.add(Retailer("Retailer C5", 8, 30))
//        retailerList.add(Retailer("Retailer A45", 10, 50))
//        retailerList.add(Retailer("Retailer B45", 5, 20))
//        retailerList.add(Retailer("Retailer C45", 8, 30))
//        retailerList.add(Retailer("Retailer A34", 10, 50))
//        retailerList.add(Retailer("Retailer B33", 5, 20))
//        retailerList.add(Retailer("Retailer C23", 8, 30))
//        retailerList.add(Retailer("Retailer A1", 10, 50))
//        retailerList.add(Retailer("Retailer B24", 5, 20))
//        retailerList.add(Retailer("Retailer C5", 8, 30))
//        retailerList.add(Retailer("Retailer A7", 10, 50))
//        retailerList.add(Retailer("Retailer B7", 5, 20))
//        retailerList.add(Retailer("Retailer C8", 8, 30))
//        retailerList.add(Retailer("Retailer A9", 10, 50))
//        retailerList.add(Retailer("Retailer B9", 5, 20))
//        retailerList.add(Retailer("Retailer C9", 8, 30))
//        retailerList.add(Retailer("Retailer A0", 10, 50))
//        retailerList.add(Retailer("Retailer B0", 5, 20))
//        retailerList.add(Retailer("Retailer C0", 8, 30))
//        retailerList.add(Retailer("Retailer A123", 10, 50))
//        retailerList.add(Retailer("Retailer B124", 5, 20))
//        retailerList.add(Retailer("Retailer C123", 8, 30))
//        retailerList.add(Retailer("Retailer A1234", 10, 50))
//        retailerList.add(Retailer("Retailer B144", 5, 20))
//        retailerList.add(Retailer("Retailer C53", 8, 30))
//        retailerList.add(Retailer("Retailer A1245", 10, 50))
//        retailerList.add(Retailer("Retailer B54", 5, 20))
//        retailerList.add(Retailer("Retailer C577", 8, 30))
//        retailerList.add(Retailer("Retailer A68", 10, 50))
//        retailerList.add(Retailer("Retailer B85", 5, 20))
//        retailerList.add(Retailer("Retailer C85", 8, 30))
//        retailerList.add(Retailer("Retailer A84", 10, 50))
//        retailerList.add(Retailer("Retailer B824", 5, 20))
//        retailerList.add(Retailer("Retailer C8433", 8, 30))
//        retailerList.add(Retailer("Retailer A3435", 10, 50))
//        retailerList.add(Retailer("Retailer B2782", 5, 20))
//        retailerList.add(Retailer("Retailer C57", 8, 30))
//        retailerList.add(Retailer("Retailer Ag", 10, 50))
//        retailerList.add(Retailer("Retailer B35", 5, 20))
//        retailerList.add(Retailer("Retailer C34f", 8, 30))

        adapter.retailersFullList = retailerList
        adapter.notifyDataSetChanged()
    }
}