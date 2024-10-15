package com.isar.imagine.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.Adapters.TransactionAdapter
import com.isar.imagine.R

class TransactionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionList: List<Transaction>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerview_transaction_history)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load dummy transaction data
        transactionList = getDummyTransactions()
        transactionAdapter = TransactionAdapter(transactionList)
        recyclerView.adapter = transactionAdapter
        // Inflate the layout for this fragment
    }
    private fun getDummyTransactions(): List<Transaction> {
        return listOf(
            Transaction(
                id = "11111111111111",
                date = "2024-08-01",
                quantity = 2,
                totalPrice = 140000.0,
                retailer = "Best",
            ),
            Transaction(
                id = "11111111111111",
                date = "2024-08-01",
                quantity = 2,
                totalPrice = 140000.0,
                retailer = "Best",
            ),
            Transaction(
                id = "11111111111111",
                date = "2024-08-01",
                quantity = 2,
                totalPrice = 140000.0,
                retailer = "Best",
            ),
            Transaction(
                id = "11111111111111",
                date = "2024-08-01",
                quantity = 2,
                totalPrice = 140000.0,
                retailer = "Best",
            ),
            Transaction(
                id = "11111111111111",
                date = "2024-08-01",
                quantity = 2,
                totalPrice = 140000.0,
                retailer = "Best",
            ),
            Transaction(
                id = "11111111111111",
                date = "2024-08-01",
                quantity = 2,
                totalPrice = 140000.0,
                retailer = "Best",
            ),
            // Add more dummy transactions as needed
        )
    }
}