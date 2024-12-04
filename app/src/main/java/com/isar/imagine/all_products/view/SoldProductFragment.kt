package com.isar.imagine.all_products.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.R
import com.isar.imagine.all_products.models.data.SoldProductInfo
import com.isar.imagine.all_products.models.repo.AllProductRepo
import com.isar.imagine.all_products.viewModels.AllProductViewModel
import com.isar.imagine.all_products.viewModels.AllProductViewModelFactory
import com.isar.imagine.databinding.FragmentSoldBinding
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView

class SoldProductFragment : Fragment() {

    private val viewModel: AllProductViewModel by viewModels {
        AllProductViewModelFactory(AllProductRepo(FirebaseFirestore.getInstance()))
    }
    private lateinit var binding: FragmentSoldBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSoldBinding.inflate(inflater, container, false)

        observers()
        return binding.root
    }

    private fun observers() {
        viewModel.soldProductLiveData.observe(viewLifecycleOwner) {
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
                    binding.recyclerviewSold.adapter = SoldAdapter(requireContext(), it.data!!){
                        val bundle = Bundle().apply {
                            putSerializable("soldProductInfo",it)
                        }
                        Navigation.findNavController(binding.root).navigate(R.id.action_allProductFragment_to_singleProductView, args = bundle)
                    }
                    binding.recyclerviewSold.layoutManager = LinearLayoutManager(requireContext())
                }
            }

        }
    }


}

private class SoldAdapter(
    private val context: Context, private val soldList: List<SoldProductInfo>,
    val onButtonClicked : (SoldProductInfo) -> Unit
) : RecyclerView.Adapter<SoldAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
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
        holder.nameTxt.text = soldProductInfo.name
        holder.brandTxt.text = soldProductInfo.brand
        try {
            Glide.with(context).load(soldProductInfo.imageUrl)
                .placeholder(R.drawable.baseline_person_24).into(holder.profileImage)
        } catch (e: Exception) {
            Log.e("TAG", "Error in GLide ${e.message}")
        }
        holder.button.setOnClickListener {
            onButtonClicked(soldProductInfo)
        }
    }

    override fun getItemCount(): Int = soldList.size
}