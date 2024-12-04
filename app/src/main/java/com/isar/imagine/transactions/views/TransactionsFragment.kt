package com.isar.imagine.transactions.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.Adapters.TransactionAdapter
import com.isar.imagine.R
import com.isar.imagine.transactions.viewmodels.TransactionRepositoryImpl
import com.isar.imagine.transactions.viewmodels.TransactionViewModelFactory
import com.isar.imagine.transactions.viewmodels.TransactionsViewModel
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView

class TransactionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private val repository: TransactionRepositoryImpl =
        TransactionRepositoryImpl(FirebaseFirestore.getInstance())
    private val viewModel: TransactionsViewModel by viewModels {
        TransactionViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.transactionsList.observe(viewLifecycleOwner) {
            when (it) {
                is Results.Error -> {
                    CustomProgressBar.dismiss()
                    CustomDialog.showAlertDialog(
                        requireContext(),
                        requireContext().getTextView("Error : ${it.message}"),
                        "Error"
                    )
                    // Handle the error
                }

                is Results.Loading -> CustomProgressBar.show(
                    requireContext(), " Loading Transanctions"
                )

                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    recyclerView = view.findViewById(R.id.recyclerview_transaction_history)
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    transactionAdapter = TransactionAdapter(it.data!!, requireContext())
                    recyclerView.adapter = transactionAdapter
                }
            }

        }
    }


}