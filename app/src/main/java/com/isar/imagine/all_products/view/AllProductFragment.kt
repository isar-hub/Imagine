package com.isar.imagine.all_products.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.all_products.models.repo.AllProductRepo
import com.isar.imagine.all_products.viewModels.AllProductViewModel
import com.isar.imagine.all_products.viewModels.AllProductViewModelFactory
import com.isar.imagine.databinding.FragmentAllProductBinding
import com.isar.imagine.utils.CommonMethods
import kotlinx.coroutines.launch


class AllProductFragment : Fragment() {

    private lateinit var binding: FragmentAllProductBinding
    private val viewModel: AllProductViewModel by viewModels {
       AllProductViewModelFactory(AllProductRepo(FirebaseFirestore.getInstance()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllProductBinding.inflate(inflater, container, false)
        val fragments = listOf(SoldProductFragment(), BilledProductFragment())
        val name = listOf("Sold Products", "Billed Products")
        val adapter = AllProductAdapter(fragments, this@AllProductFragment)
        binding.viewPagerProduct.adapter = adapter

        TabLayoutMediator(binding.allProductTabLayout, binding.viewPagerProduct) { tab, position ->
            tab.text = name[position]
        }.attach()

        return binding.root
    }

    inner class AllProductAdapter(
        private val fragments: List<Fragment>,
        fragmentManager: Fragment
    ) : FragmentStateAdapter(fragmentManager) {

        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        lifecycleScope.launch {
//            val billed = viewModel.repository.getAllBilledProducts()
//            val  sold = viewModel.repository.getAllSoldProducts()
//
//            CommonMethods.showLogs("ALLPRODUCT","Billed ${billed.data}")
//            CommonMethods.showLogs("ALLPRODUCT","Sold ${sold.data}")
//        }

    }
}

