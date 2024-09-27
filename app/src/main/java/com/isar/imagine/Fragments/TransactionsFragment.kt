package com.isar.imagine.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.isar.imagine.Adapters.TransactionAdapter
import com.isar.imagine.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class TransactionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionList: List<Transaction>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)


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
                id = "1",
                date = "2024-08-01",
                brand = "Samsung",
                model = "Galaxy S21",
                variant = "8GB/128GB",
                color = "Phantom Gray",
                quantity = 2,
                price = 70000.0,
                totalPrice = 140000.0,
                condition = "Best",
                description = "Purchased for office use"
            ),
            Transaction(
                id = "2",
                date = "2024-07-30",
                brand = "Apple",
                model = "iPhone 13",
                variant = "256GB",
                color = "Midnight",
                quantity = 1,
                price = 80000.0,
                totalPrice = 80000.0,
                condition = "Good",
                description = "Gift for family"
            ),
            // Add more dummy transactions as needed
        )
    }

}