package com.isar.imagine.Fragments

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.isar.imagine.utils.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.isar.imagine.Adapters.Retailer
import com.isar.imagine.Adapters.RetailerAdapter
import com.isar.imagine.R
    import com.isar.imagine.databinding.FragmentRetailerBinding
import com.isar.imagine.inventory.models.DataClass
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.viewmodels.RetailerFragmentViewHolder
import com.isar.imagine.viewmodels.RetailerRepository
import org.w3c.dom.Text


class RetailerFragment() : Fragment() {

    private lateinit var adapter: RetailerAdapter
    private lateinit var binding: FragmentRetailerBinding
    private lateinit var firebaseAuth: FirebaseAuth
    val repository = RetailerRepository(FirebaseAuth.getInstance())

    private val viewModel: RetailerFragmentViewHolder by viewModels() {
        RetailerViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRetailerBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()  // Initialize FirebaseAuth here
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddRetailer.setOnClickListener { showCreateUserDialog() }
        observers()
    }



    private fun observers() {
        viewModel.retailerList.observe(viewLifecycleOwner){ retailer ->
            when(retailer){
                is Results.Error -> CustomDialog.showAlertDialog(requireContext(),requireContext().getTextView(retailer.message!!),"Error")
                is Results.Loading -> CustomProgressBar.show(requireContext(),"Loading Retailers")
                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    val retailer = retailer.data?.map { it.displayName }
                    setupRecyclerView(retailer!!)
                }
            }
        }

    }

    private fun setupRecyclerView(retailers: List<String>) {
        adapter = RetailerAdapter(retailers){

        }
        binding.rvRetailers.layoutManager = LinearLayoutManager(context)
        binding.rvRetailers.adapter = adapter
    }
    private fun showCreateUserDialog() {
        firebaseAuth = FirebaseAuth.getInstance()

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_user, null)

        // Create the AlertDialog
        val dialog = context?.let {
            AlertDialog.Builder(it)
                .setView(dialogView)
                .setCancelable(true)
                .create()
        }

        // Get references to the views in the dialog layout
        val emailEditText = dialogView.findViewById<EditText>(R.id.edit_email)
        val passwordEditText = dialogView.findViewById<EditText>(R.id.edit_password)
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_name)
        val createButton = dialogView.findViewById<Button>(R.id.submit)

        // Handle Create User button click
        createButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()+"@imagine.com"
            val password = passwordEditText.text.toString().trim()
            val name = nameEditText.text.toString().trim()

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
}

class RetailerViewModelFactory(
    private val repository: RetailerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RetailerFragmentViewHolder::class.java)) {
            return RetailerFragmentViewHolder(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
