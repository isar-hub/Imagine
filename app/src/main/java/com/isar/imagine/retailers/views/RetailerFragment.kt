package com.isar.imagine.retailers.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.auth.FirebaseAuth
import com.isar.imagine.R
import com.isar.imagine.databinding.FragmentRetailerBinding
import com.isar.imagine.retailers.data.UserDetails
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView
import com.isar.imagine.retailers.viewmodels.RetailerFragmentViewHolder
import com.isar.imagine.retailers.viewmodels.RetailerRepository
import com.isar.imagine.retailers.data.dao.UserDatabase


class RetailerFragment : Fragment() {

    private lateinit var adapter: RetailerAdapter
    private lateinit var binding: FragmentRetailerBinding
    private val repository: RetailerRepository by lazy {
        RetailerRepository(
            FirebaseAuth.getInstance(), UserDatabase.getDatabase(requireContext()).userDao()
        )
    }

    private val viewModel: RetailerFragmentViewHolder by viewModels {
        RetailerViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRetailerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddRetailer.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_retailerFragment_to_userDetailsActivity)
        }


        binding.sendNotification.setOnClickListener {
            showCreateUserDialog()
        }
        parentFragmentManager.setFragmentResultListener(
            "refreshKey",
            viewLifecycleOwner
        ) { key, bundle ->
            val isRefresh = bundle.getBoolean("refresh")
            if (isRefresh) {
                viewModel.getRetailers()
            }
        }
        searchView()
        observers()
    }

    private fun searchView() {
        binding.svRetailerSearch.setOnClickListener { binding.svRetailerSearch.onActionViewExpanded() }

        binding.svRetailerSearch.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText);
                return true
            }

        })
    }


    private fun observers() {
        viewModel.retailerList.observe(viewLifecycleOwner) { retailer ->
            when (retailer) {
                is Results.Error -> {
                    CustomDialog.showAlertDialog(
                        requireContext(), requireContext().getTextView(retailer.message!!), "Error"
                    )
                    CustomProgressBar.dismiss()
                }

                is Results.Loading -> CustomProgressBar.show(requireContext(), "Loading Retailers")
                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    setupRecyclerView(retailer.data!!)
                }

                else -> {
                    CustomDialog.showAlertDialog(
                        requireContext(), requireContext().getTextView(retailer.message!!), "Error"
                    )
                }
            }
        }

    }

    private fun setupRecyclerView(retailers: List<UserDetails>) {
        adapter = RetailerAdapter(retailers) { userDetails ->
            val bundle = Bundle()
            bundle.putSerializable("userDetails", userDetails)
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_retailerFragment_to_userDetailsActivity, args = bundle)
        }
        binding.rvRetailers.layoutManager = LinearLayoutManager(context)
        binding.rvRetailers.adapter = adapter
        if (adapter.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
            binding.rvRetailers.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.rvRetailers.visibility = View.VISIBLE
        }
    }

    private fun showCreateUserDialog() {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_user, null)

        // Create the AlertDialog
        val dialog = context?.let {
            AlertDialog.Builder(it).setView(dialogView).setCancelable(true).create()
        }

        // Get references to the views in the dialog layout
        val title = dialogView.findViewById<EditText>(R.id.title)
        val message = dialogView.findViewById<EditText>(R.id.message)
        val createButton = dialogView.findViewById<Button>(R.id.send)
        val loader = dialogView.findViewById<CircularProgressIndicator>(R.id.loaderDialog)

        // Handle Create User button click
        createButton.setOnClickListener {
            val titleText = title.text.toString().trim()
            val messageText = message.text.toString().trim()

            if (titleText.isEmpty()) {
                title.error = "Title cannot be empty"
            } else if (messageText.isEmpty()) {
                message.error = "Message cannot be empty"
            } else {
                if (dialog != null) {
                    viewModel.sendNotification(titleText, messageText) {
                        when (it) {
                            is Results.Success -> {
                                dialog.hide()
                                CustomDialog.showAlertDialog(
                                    requireContext(),
                                    requireContext().getTextView(it.data!!),
                                    "Success"
                                )

                            }

                            is Results.Error -> {
                                dialog.hide()
                                CustomDialog.showAlertDialog(
                                    requireContext(),
                                    requireContext().getTextView(it.message!!),
                                    "Error"
                                )

                            }

                            is Results.Loading -> {
                                loader.visibility = View.VISIBLE
                                createButton.visibility = View.GONE
                            }

                        }
                    }
                }
            }
        }

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
