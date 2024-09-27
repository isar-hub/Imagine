package com.isar.imagine.Fragments

import StackViewAdapter
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.StackView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.isar.imagine.R
import com.isar.imagine.responses.BrandNameResponseItem
import com.isar.imagine.responses.DeviceInformation
import com.isar.imagine.responses.InventoryCountResponses
import com.isar.imagine.responses.StackViewList
import com.isar.imagine.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Random


class DashboardFragment : Fragment() {



    lateinit var totalItemTextView : TextView
    lateinit var stackView : StackView
    lateinit var totalSoldTextView : TextView
    lateinit var deviceNameOptions : AutoCompleteTextView
    lateinit var deviceModelOptions : AutoCompleteTextView
    lateinit var quantityEditText : EditText
    lateinit var submitButton : Button
    lateinit var stackViewAdapter: StackViewAdapter
    lateinit var myView: View
    private lateinit var myDB: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        this.myView = view
        initialization(view)

        submitButton.setOnClickListener {

            val num = generateRandom16DigitNumber()



            if (!deviceNameOptions.text.isEmpty() && !deviceModelOptions.text.isEmpty() && !quantityEditText.text.isEmpty()) {
                var number = generateRandom16DigitNumber()
                val device = DeviceInformation(
                    deviceNameOptions.text.toString(),
                    deviceModelOptions.text.toString(),
                    "Good",
                    "10000",
                    quantityEditText.text.toString()
                )
                myDB.child(number).setValue(device).addOnCompleteListener {
                    genBarcode(number)
                    deviceModelOptions.clearListSelection()
                    deviceNameOptions.clearListSelection()
                    quantityEditText.text.clear()
                }


            } else Snackbar.make(view, "Some Error Occurred", Snackbar.LENGTH_LONG)
                .setAction("OK") {
                    // Handle the action here
                    // e.g., retrying an operation
                }.show()
        }

    }

    private fun initialization(view : View) {

        stackView = view.findViewById(R.id.idStackView)
        totalItemTextView = view.findViewById(R.id.totalItem)
        totalSoldTextView = view.findViewById(R.id.totalSold)
        deviceNameOptions = view.findViewById(R.id.device_name)
        deviceModelOptions = view.findViewById(R.id.device_model)
        submitButton = view.findViewById(R.id.button_first)
        quantityEditText = view.findViewById(R.id.quantity)


        stackView()


        val database = Firebase.database
        myDB = database.getReference("Device")


        val languages = resources.getStringArray(R.array.programming_languages)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, languages)

        deviceNameOptions.setAdapter(arrayAdapter)


        val languages1 = resources.getStringArray(R.array.device_model)
        getAllUserIds()
        val arrayAdapter1 =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu, languages1)
        deviceModelOptions.setAdapter(arrayAdapter1)




    }



    fun generateRandom16DigitNumber(): String {
        val random = Random()

        // Define the lower bound (smallest 16-digit number) and upper bound (largest 16-digit number)
        val lowerBound = 1000000000000000L
        val upperBound = 9999999999999999L
        // Generate a random long value within the specified rang
        val number = random.nextLong()
        return number.toString()
    }

    private fun getAllUserIds() {
        // Attach a listener to read the data at the "users" reference
        myDB.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val userIds = mutableListOf<String>()
                    for (userSnapshot in snapshot.children) {
                        // Get the user ID (key) of each child
                        val userId = userSnapshot.key
                        if (userId != null) {

                            userIds.add(userId)
                        }
                    }

                    Log.d("Test", "list ${userIds}")

                    // Handle the list of user IDs

                } else {
                    Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("test","error")
            }


        })
    }

    private fun stackView() {

        var list: InventoryCountResponses? = null

        val brandCountCall = RetrofitInstance.getApiInterface().getItemCount()
        brandCountCall.enqueue(object : Callback<InventoryCountResponses> {
            override fun onResponse(
                call: Call<InventoryCountResponses>, response: Response<InventoryCountResponses>
            ) {
                if (response.isSuccessful && response.body() != null) {

                    list = response.body()
                    Log.e("Failure", "Error in getting item Count : ${list?.totalBrands}")
                    val numberList: ArrayList<StackViewList> = ArrayList()

                    val temp: String = getString(
                        R.string.totalCountPlaceHolder,
                        list?.totalBrands.toString()
                    )
                    totalItemTextView.text = temp

                    list?.brands?.forEach { brand ->
                        Log.e("test", "item ${brand.name + brand.totalModels}")
                        numberList.add(StackViewList(brand.name, brand.totalModels))
                    }
                    Log.e("test", "item $numberList")

                    // Setting up the adapter for StackView
                    stackViewAdapter = StackViewAdapter(
                        context!!, R.layout.category_stocks, numberList
                    )
                    stackView.adapter = stackViewAdapter

                }
            }

            override fun onFailure(call: Call<InventoryCountResponses>, t: Throwable) {
                Log.e("Failure", "Error in getting item Count : ${t.message}")
            }

        })


    }

    private fun getExampleData() {
        val call = RetrofitInstance.getApiInterface().getExampleData()
        call.enqueue(object : Callback<ArrayList<BrandNameResponseItem>> {
            override fun onResponse(
                call: Call<ArrayList<BrandNameResponseItem>>,
                response: Response<ArrayList<BrandNameResponseItem>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    // TODO: Process data
                    Log.d("RetrofitTest", "response size ${response.body()!!.size}")
                    Log.d("RetrofitTest", "response 1st ${response.body()!!.get(0)}")
                } else {
                    Log.d("RetrofitTest", "response failed 1${response.code()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<BrandNameResponseItem>>, t: Throwable) {
                Log.d("RetrofitTest", "response failed ${t.message}")
            }
        })
    }


    private fun genBarcode(inputValue: String) {

        if (inputValue.isNotEmpty()) {
            // Initializing a MultiFormatWriter to encode the input value
            val mwriter = MultiFormatWriter()

            try {
                // Generating a barcode matrix
                val matrix = mwriter.encode(inputValue.toString(), BarcodeFormat.CODE_128, 200, 200)

                // Creating a bitmap to represent the barcode
                val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565)

                // Iterating through the matrix and set pixels in the bitmap
                for (i in 0 until 200) {
                    for (j in 0 until 200) {
                        bitmap.setPixel(i, j, if (matrix[i, j]) Color.BLACK else Color.WHITE)
                    }
                }

                // Seting the bitmap as the image resource of the ImageView
                saveImage(bitmap)
            } catch (e: Exception) {

                Snackbar.make(myView, "Exception $e", Snackbar.LENGTH_LONG).setAction("OK") {
                    // Handle the action here
                    // e.g., retrying an operation
                }.show()
            }
        } else {
            // Showing an error message if the EditText is empty
            Snackbar.make(myView, "Error in getting ID ", Snackbar.LENGTH_LONG)
                .setAction("OK") {
                    // Handle the action here
                    // e.g., retrying an operation
                }.show()
        }
    }

    fun saveImage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            requireContext().contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)

            Snackbar.make(myView, "Saved to Photos", Snackbar.LENGTH_LONG).setAction("OK") {
                // Handle the action here
                // e.g., retrying an operation
            }.show()

        }
    }


}