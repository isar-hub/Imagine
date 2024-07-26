package com.isar.imagine

import StackViewAdapter
import android.R
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.StackView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.isar.imagine.databinding.FragmentFirstBinding
import com.isar.imagine.responses.BrandNameResponseItem
import com.isar.imagine.responses.DeviceInformation
import com.isar.imagine.responses.StackViewList
import com.isar.imagine.retrofit.ApiInterface
import com.isar.imagine.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.Random


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainPanelFragment : Fragment() {
    private lateinit var apiInterface: ApiInterface

    private var _binding: FragmentFirstBinding? = null
    private var stackView: StackView? = null
    private lateinit var context : Context
    private lateinit var myDB: DatabaseReference

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




    stackView()








        val database = Firebase.database
         myDB = database.getReference("Device")
//
//        myDB.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val value = snapshot.value
//                Log.d("Test", "Value is: " + value)            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })


        val languages = resources.getStringArray(com.isar.imagine.R.array.programming_languages)
        val arrayAdapter = ArrayAdapter(context, com.isar.imagine.R.layout.dropdown_menu, languages)
        // get reference to the autocomplete text view
        val autocompleteTV = view.findViewById<AutoCompleteTextView>(com.isar.imagine.R.id.device_name)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)




        val languages1 = resources.getStringArray(com.isar.imagine.R.array.device_model)
        getAllUserIds()


        val arrayAdapter1 = ArrayAdapter(context, com.isar.imagine.R.layout.dropdown_menu, languages1)
        // get reference to the autocomplete text view
        val autocompleteTV1 = view.findViewById<AutoCompleteTextView>(com.isar.imagine.R.id.device_model)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV1.setAdapter(arrayAdapter1)
        val edittext = view.findViewById<EditText>(com.isar.imagine.R.id.quantity)






        binding.buttonFirst.setOnClickListener{

            val num = generateRandom16DigitNumber()



            if (!autocompleteTV.text.isEmpty() && !autocompleteTV1.text.isEmpty() && !edittext.text.isEmpty()){
                var number = generateRandom16DigitNumber()
                val  device = DeviceInformation(autocompleteTV.text.toString(),
                    autocompleteTV1.text.toString(),"Good","10000", edittext.text.toString()
                )
                myDB.child(number).setValue(device).addOnCompleteListener{
                    genBarcode(number)
                    autocompleteTV.clearListSelection()
                    autocompleteTV1.clearListSelection()
                    edittext.text.clear()
                }


            }else
                Snackbar.make(view, "Some Error Occurred", Snackbar.LENGTH_LONG)
                    .setAction("OK") {
                        // Handle the action here
                        // e.g., retrying an operation
                    }
                    .show()
        }

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

                    Log.d("Test","list ${userIds}")

                    // Handle the list of user IDs

                } else {
                    Toast.makeText(context, "No users found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }


    private fun getExampleData(){
        val call = RetrofitInstance.getApiInterface().getExampleData()
        call.enqueue(object : Callback<ArrayList<BrandNameResponseItem>> {
            override fun onResponse(call: Call<ArrayList<BrandNameResponseItem>>, response: Response<ArrayList<BrandNameResponseItem>>) {
                if (response.isSuccessful && response.body()!=null){
                    // TODO: Process data
                    Log.d("RetrofitTest","response size ${response.body()!!.size}")
                    Log.d("RetrofitTest","response 1st ${response.body()!!.get(0)}")
                }
                else{
                    Log.d("RetrofitTest","response failed 1${response.code()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<BrandNameResponseItem>>, t: Throwable) {
                Log.d("RetrofitTest","response failed ${t.message}")
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun genBarcode(inputValue :String) {

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

                view?.let {
                    Snackbar.make(it, "Exception $e", Snackbar.LENGTH_LONG).setAction("OK") {
                        // Handle the action here
                        // e.g., retrying an operation
                    }
                        .show()
                }
            }
        } else {
            // Showing an error message if the EditText is empty
            view?.let {
                Snackbar.make(it, "Error in getting ID ", Snackbar.LENGTH_LONG).setAction("OK") {
                    // Handle the action here
                    // e.g., retrying an operation
                }
                    .show()
            }
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
            context?.contentResolver?.also { resolver ->

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

            view?.let { it1 ->
                Snackbar.make(it1, "Saved to Photos", Snackbar.LENGTH_LONG)
                    .setAction("OK") {
                        // Handle the action here
                        // e.g., retrying an operation
                    }
                    .show()
            }

        }
    }


    fun stackView(){

        // initializing variables for stack view on below line.

        stackView = binding.idStackView

        // creating a variable for list and adding data to it.
        val numberList: ArrayList<StackViewList> = ArrayList()
        numberList.add(StackViewList("samsung",1))
        numberList.add(StackViewList("xiamoi",1434))
        numberList.add(StackViewList("vivo",231476))
        numberList.add(StackViewList("oneplus",234))
        numberList.add(StackViewList("apple",1))
        numberList.add(StackViewList("samsung",1))
        numberList.add(StackViewList("xiamoi",1434))
        numberList.add(StackViewList("vivo",231476))
        numberList.add(StackViewList("oneplus",234))
        numberList.add(StackViewList("apple",1))


        val stackViewAdapter : StackViewAdapter= StackViewAdapter(context,com.isar.imagine.R.layout.category_stocks,numberList)
        stackView!!.adapter = stackViewAdapter



    }
}