package com.isar.imagine.warranty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.isar.imagine.databinding.FragmentWarrantyBinding


class WarrantyFragment : Fragment() {

    private lateinit var binding: FragmentWarrantyBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentWarrantyBinding.inflate(inflater,container,false)

        val fragments = listOf(ClaimedWarranty(), WarrantyHistory())
        val name = listOf("Warranty Claimed","Warranty History")
        val adapter = WarrantyAdapter(fragments, this@WarrantyFragment)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab,position->
            tab.text = name[position]
        }.attach()

        return binding.root
    }




}
 private class WarrantyAdapter(private val fragments: List<Fragment>,fragmentManager: Fragment) : FragmentStateAdapter(fragmentManager){

     override fun getItemCount(): Int = fragments.size

     override fun createFragment(position: Int): Fragment = fragments[position]

 }