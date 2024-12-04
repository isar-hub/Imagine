package com.isar.imagine.warranty.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.isar.imagine.databinding.FragmentClaimedWarrantyBinding
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView
import com.isar.imagine.warranty.views.adapters.ClaimedWarrantyAdapter
import com.isar.imagine.warranty.viewmodel.ClaimedWarrantyViewModel
import com.isar.imagine.warranty.data.ClaimedModel


class ClaimedWarranty : Fragment() {
    private lateinit var binding:FragmentClaimedWarrantyBinding
    private val viewModel: ClaimedWarrantyViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the ViewModel's warranty data here
        viewModel.warranty.observe(viewLifecycleOwner) { result ->

            Log.e("WARRANT","STATUS : $result")
            when (result) {
                is Results.Error -> {
                    CustomProgressBar.dismiss()
                    binding.emptyView.visibility = View.VISIBLE
                    showDialog("Error ${result.message}")
                }
                is Results.Loading -> {
                    CustomProgressBar.show(requireContext(), "Loading....")
                }
                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    setUpRecyclerView(result.data!!) // It's safe to call this here
                }
            }
        }
    }
    private fun setUpRecyclerView(warranty : List<ClaimedModel>){
        val recycler = ClaimedWarrantyAdapter(requireContext(),warranty)
        binding.recyclerviewTransactionHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewTransactionHistory.adapter = recycler
    }

    private fun showDialog(msg : String){
        CustomDialog.showAlertDialog(requireContext(),requireContext().getTextView(msg),"Error")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClaimedWarrantyBinding.inflate(inflater, container, false)

        return binding.root
    }


}