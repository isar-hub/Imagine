package com.isar.imagine

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.isar.imagine.all_products.models.data.BilledProductInfo
import com.isar.imagine.all_products.models.data.SoldProductInfo
import com.isar.imagine.databinding.FragmentSingleProductViewBinding
import com.isar.imagine.utils.CommonMethods


class SingleProductView : Fragment() {

    private lateinit var binding: FragmentSingleProductViewBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSingleProductViewBinding.inflate(inflater, container, false)

        val data = arguments?.getSerializable("soldProductInfo")
        if (data is SoldProductInfo) {
            bindSoldProductInfo(data)
            CommonMethods.showLogs(
                "SOLD",
                "Data is  a SoldProductInfo"
            )
        }
        else if (data is BilledProductInfo){
            bindBilledProductInfo(data)
            CommonMethods.showLogs(
                "SOLD",
                "Data is  a Billed"
            )
        }
        else {
            CommonMethods.showLogs(
                "SOLD",
                "Data is not a SoldProductInfo"
            )  // Handle the error here or return null or throw an exception.  // For example, you can return a default view or throw an exception.  // Note that this will prevent the app from crashing.  // This is just an example. In a real-world application, you should handle the error properly.  // For example, you can log the error, display an error message, or perform some fallback action.  // For example, you can display a default image or text, or return a default value.  // In this case, I've just returned null.  // You can replace this with your own error handling logic.  // In a real-world application, you should replace this with your own error handling logic.  // For example, you can log the error, display an error message, or perform some fallback action.  // For example, you can'
        }
        return binding.root
    }

    private fun bindBilledProductInfo(data: BilledProductInfo) {
        binding.serialNumber.text = data.serialNumber
        binding.brand.text = data.brand
        binding.model.text = data.model
        binding.variant.text = data.variant
        binding.condition.text = data.condition
        binding.sellingPrice.text = data.sellingPrice.toString()


        binding.userInformationLayout.visibility = View.GONE

//        binding.warrantyStart.text = data.warrantyStarted
//        binding.warrantyEnd.text = data.warrantyEnded
//        binding.notes.text = data.notes
//        binding.retailerId.text = data.retailerId
//        binding.status.text = data.status.name
//        binding.totalPrice.text = data.totalPrice

        // For image and signature URLs, you may load them using an image loading library like Glide or Picasso
//        Glide.with(binding.root.context)
//            .load(data.imageUrl)
//            .into(binding.userImage)
//
//        Glide.with(binding.root.context)
//            .load(data.signatureUrl).into(binding.signatureImage)
    }

    private fun bindSoldProductInfo(data: SoldProductInfo) {

        binding.serialNumber.text = data.serialNumber
        binding.brand.text = data.brand
        binding.model.text = data.model
        binding.variant.text = data.variant
        binding.condition.text = data.condition
        binding.sellingPrice.text = data.sellingPrice

        binding.userName.text = data.name
        binding.userPhone.text = data.phone
        binding.userEmail.text = data.email
        binding.userAddress.text = data.address
        binding.userState.text = data.state

//        binding.warrantyStart.text = data.warrantyStarted
//        binding.warrantyEnd.text = data.warrantyEnded
//        binding.notes.text = data.notes
//        binding.retailerId.text = data.retailerId
//        binding.status.text = data.status.name
//        binding.totalPrice.text = data.totalPrice

        // For image and signature URLs, you may load them using an image loading library like Glide or Picasso
        Glide.with(binding.root.context)
            .load(data.imageUrl)
            .into(binding.userImage)

        Glide.with(binding.root.context)
            .load(data.signatureUrl).into(binding.signatureImage)


    }


}