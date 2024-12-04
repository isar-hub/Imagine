package com.isar.imagine.warranty.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.databinding.FragmentProductViewBinding
import com.isar.imagine.databinding.ImageViewItemBinding
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ProductView : Fragment() {

    private lateinit var binding: FragmentProductViewBinding
    private val viewModel: ProductViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductViewBinding.inflate(inflater, container, false)

        val serialNumber = arguments?.getString("SERIAL_NUMBER")
        CommonMethods.showLogs("WARRANTY", "serial number : $serialNumber")
        if (serialNumber != null) {
            viewModel.fetchProductDetails(serialNumber)
        }
        viewModel.productData.observe(viewLifecycleOwner) {
            CommonMethods.showLogs("WARRANTY", " Result is $it")
            when (it) {
                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    bindData(it.data!!)
                }

                is Results.Loading -> {
                    CustomProgressBar.show(requireContext(), "Loading..")
                }

                is Results.Error -> {
                    CustomProgressBar.dismiss()
                    binding.emptyView.apply {
                        visibility = View.VISIBLE
                        text = it.message!!
                    }


                }
            }
        }

        return binding.root
    }

    private fun bindData(productDetails: ProductDetails) {
        binding.brand.text = productDetails.brand
        binding.model.text = productDetails.model
        binding.variant.text = productDetails.variant
        binding.condition.text = productDetails.condition
        binding.sellingPrice.text = productDetails.sellingPrice
        binding.serialNumber.text = productDetails.serialNumber
        binding.soldOn.text = productDetails.warrantyStarted


        bindImage(binding.userImage, productDetails.imageUrl)
        bindImage(binding.signatureImage, productDetails.signatureUrl)

        binding.userName.text = productDetails.name
        binding.userPhone.text = productDetails.phone
        binding.userEmail.text = productDetails.email
        binding.userState.text = productDetails.state
        binding.userAddress.text = productDetails.address


        binding.warrantyStarted.text = productDetails.warrantyStarted
        binding.warrantyExpired.text = productDetails.warrantyEnded
        binding.reason.text = productDetails.warranty[0].reason
        binding.reasonDescription.text = productDetails.warranty[0].reasonDescription


        val adapter = ImageViewAdapter(requireContext(), productDetails.warranty[0].images)
        if (adapter.imageList.isEmpty()){
            binding.imgRecyclerVIew.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        }
        binding.imgRecyclerVIew.adapter = adapter
        binding.imgRecyclerVIew.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    }

    fun bindImage(
        imageView: ImageView, imageUrl: String
    ) {
        Glide.with(imageView.context).load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_report_image).into(imageView)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
    }


    companion object {
        fun newInstance(serialNumber: String): Bundle {
            val bundle = Bundle().apply {
                putString("SERIAL_NUMBER", serialNumber)
            }
            return bundle
        }
    }
}

class ProductViewModel : ViewModel() {
    private val _productData =
        MutableLiveData<Results<ProductDetails>>(Results.Loading()) // Replace String with your actual data model
    val productData: LiveData<Results<ProductDetails>> get() = _productData
    fun fetchProductDetails(serialNumber: String) {
        viewModelScope.launch {
            try {
                val firestore =
                    FirebaseFirestore.getInstance().collection("products").document(serialNumber)
                        .get().await()
                if (firestore.exists()) {
                    CommonMethods.showLogs("WARRANTY", "Firestore is successfull ")
                    val productData = firestore.toObject(ProductDetails::class.java)
                    CommonMethods.showLogs("WARRANTY", "Product is $productData")
                    _productData.value = Results.Success(productData!!)
                } else {
                    CommonMethods.showLogs("WARRANTY", "No successfull")
                    _productData.value = Results.Error("No Product Found ")
                }
            } catch (e: Exception) {
                CommonMethods.showLogs("WARRANTY", "Exception and ${e.message}")
                _productData.value = Results.Error("Error ${e.message}")
            }


        }
    }
}

data class ProductDetails(
    // Product details
    val serialNumber: String = "",
    val brand: String = "",
    val model: String = "",
    val variant: String = "",
    val condition: String = "",
    val sellingPrice: String = "",
    val warrantyEnded: String = "",
    val warrantyStarted: String = "",
    // User details
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val address: String = "",
    val state: String = "",
    val imageUrl: String = "",
    val signatureUrl: String = "",

    // Retailer details
    val notes: String = "",
    val retailerId: String = "",
    val status: String = "",

    val warranty: List<Warranty> = emptyList()
) {
    data class Warranty(
        val createdAt: String = "",
        val id: String = "",
        val reason: String = "",
        val reasonDescription: String = "",
        val status: String = "",

        val images: List<String> = emptyList()
    )
}

class ImageViewAdapter(private val mContext: Context, val imageList: List<String>) :
    RecyclerView.Adapter<ImageViewAdapter.MyViewHolder>() {

    class MyViewHolder(var view: ImageViewItemBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ImageViewItemBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val image = imageList[position]
        val view = holder.view

        try {
            Glide.with(mContext).load(image).placeholder(android.R.drawable.ic_menu_report_image).into(view.image)
        }catch (e : Exception){
            CommonMethods.showLogs("WARRANTY", "Exception and ${e.message}")
            view.image.setImageResource(android.R.drawable.ic_menu_report_image)
        }



    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}