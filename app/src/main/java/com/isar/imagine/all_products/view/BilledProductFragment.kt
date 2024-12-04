package com.isar.imagine.all_products.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.R
import com.isar.imagine.all_products.models.data.BilledProductInfo
import com.isar.imagine.all_products.models.data.SoldProductInfo
import com.isar.imagine.all_products.models.repo.AllProductRepo
import com.isar.imagine.all_products.viewModels.AllProductViewModel
import com.isar.imagine.all_products.viewModels.AllProductViewModelFactory
import com.isar.imagine.databinding.FragmentAllProductBinding
import com.isar.imagine.databinding.FragmentBilledProductBinding
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView


class BilledProductFragment : Fragment() {
    private val viewModel: AllProductViewModel by viewModels {
        AllProductViewModelFactory(AllProductRepo(FirebaseFirestore.getInstance()))
    }
    private lateinit var binding: FragmentBilledProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBilledProductBinding.inflate(inflater,container,false)
        observers()
        return binding.root
    }

    private fun observers() {
        viewModel.billedProductLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Results.Error -> {
                    CustomProgressBar.dismiss()
                    CustomDialog.showAlertDialog(
                        requireContext(), requireContext().getTextView("${it.message}"), "Error"
                    )
                }

                is Results.Loading -> {
                    CustomProgressBar.show(requireContext(), "Loading Products")
                }

                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    binding.recyclerviewSold.adapter = BilledAdapter(requireContext(), it.data!!){
                        val bundle = Bundle().apply {
                            putSerializable("billedProductInfo",it)
                        }
                        Navigation.findNavController(binding.root).navigate(R.id.action_allProductFragment_to_singleProductView, args = bundle)

                    }
                    binding.recyclerviewSold.layoutManager = LinearLayoutManager(requireContext())
                }
            }

        }
    }

}

private class BilledAdapter(
    private val context: Context, private val soldList: List<BilledProductInfo>,
    val onButtonClicked : (BilledProductInfo) -> Unit
) : RecyclerView.Adapter<BilledAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serialNumberTxt: TextView = itemView.findViewById(R.id.serialNumber)
        val nameTxt: TextView = itemView.findViewById(R.id.name)
        val brandTxt: TextView = itemView.findViewById(R.id.brand)
        val button : ImageButton = itemView.findViewById(R.id.buttonView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.sold_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val soldProductInfo = soldList[position]


        holder.serialNumberTxt.text = soldProductInfo.serialNumber
        holder.nameTxt.text = soldProductInfo.brand
        holder.brandTxt.text = soldProductInfo.sellingPrice.toString()
        holder.button.setOnClickListener {
            onButtonClicked(soldProductInfo)
        }

    }

    override fun getItemCount(): Int = soldList.size
}