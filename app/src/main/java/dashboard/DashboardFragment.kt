package dashboard

import DashboardViewmodel
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.R
import com.isar.imagine.databinding.FragmentDashboard2Binding
import com.isar.imagine.responses.InventoryCountResponses
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import dashboard.models.BarChartModel
import dashboard.models.PieChartModel
import java.util.ArrayList
import java.util.Random


class DashboardFragment : Fragment() {

    private lateinit var myDB: DatabaseReference
    private lateinit var binding: FragmentDashboard2Binding
    private lateinit var viewModel : DashboardViewmodel;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboard2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel = DashboardViewmodel()


        horizontalBarChartData()
        lineCharData()


        onbserveData()
    }

    fun onbserveData(){
        viewModel.brands.observe(viewLifecycleOwner){
            when(it){
                is Results.Loading ->{
                  CustomProgressBar.show(requireContext(),"Loading Model ...")
                }
                is Results.Success ->{
                    barChart(it.data!!)
                    calculateTotalStock(it.data.quantity)
                    CustomProgressBar.dismiss()
                }
                is Results.Error ->{}

            }
        }
        viewModel.conditionWise.observe(viewLifecycleOwner){
            when(it){
                is Results.Loading ->{
                    CustomProgressBar.show(requireContext(),"Loading Model ...")
                }
                is Results.Success ->{
                    pieChartData(it.data!!)
                    CustomProgressBar.dismiss()
                }
                is Results.Error ->{}

            }
        }

    }

    private fun calculateTotalStock(quantity: List<Float>) {
        val total =  quantity.sum()
        binding.totalSum.text = "Total item : $total"
    }


    fun  barChart(data : BarChartModel){
        // Assuming you're using MPAndroidChart
        val barChart1 = binding.salesBarChart
        val barChart = binding.modelWiseStockBarChart


        val list = ArrayList<BarEntry>()
        for (i in data.quantity.indices) {
            list.add(BarEntry(i.toFloat(), data.quantity[i],data.brandName[i]))
        }

        val set = BarDataSet(list, "Phone Models Stock").apply {
            color = R.color.ColorPrimary // Set the bar color
            valueTextColor = R.color.white // Color of value text on bars
            valueTextSize = 16f // Size of value text on bars
        }

        val barData = BarData(set).apply {
            barWidth = 0.4f // Adjust bar width as needed
        }

        // Set the data to the chart
        barChart.data = barData
        barChart1.data = barData

        // Customize the chart appearance
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(data.brandName) // Set model names on X-axis
        barChart.xAxis.granularity = 1f
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000)

        // Refresh the chart
        barChart.invalidate()
        barChart1.invalidate()


    }


    fun pieChartData(data : PieChartModel){

        val pieChart1 = binding.conditionWiseSalesPieChart
        val pieChart = binding.conditionWiseStockPieChart



        // Create a list of PieEntry objects
        val pieEntries =  data.condition.mapIndexed{ index, condition ->
            PieEntry(data.quantity[index],condition)
        }

        // Create a PieDataSet
        val dataSet = PieDataSet(pieEntries, "Condition Wise Stock").apply {
            colors = listOf(Color.YELLOW, Color.LTGRAY, Color.DKGRAY) // Set colors for each condition
            valueTextColor = Color.BLACK // Color of value text
            valueTextSize = 16f // Size of value text
        }

        // Create PieData and set it to the chart
        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart1.data = pieData

        // Customize the chart appearance
        pieChart.setUsePercentValues(true) // Show values in percentages
        pieChart.description.isEnabled = false // Disable the description
        pieChart.legend.isEnabled = true // Enable legend
        pieChart.animateY(1000) // Add animation for pie chart

        // Refresh the chart
        pieChart.invalidate() // Refresh the chart to reflect the changes
        pieChart1.invalidate()

    }


    fun horizontalBarChartData(){
        // Initialize your HorizontalBarChart
        val horizontalBarChart = binding.areaWiseHorizontalChart

// Sample data for phone conditions
        val conditionNames = listOf("Gold", "Silver", "Platinum")
        val conditionValues = listOf(150f, 80f, 30f)

// Create a list of BarEntry objects for horizontal bar chart
        val barEntries = ArrayList<BarEntry>()
        for (i in conditionValues.indices) {
            barEntries.add(BarEntry(conditionValues[i], i.toFloat()))
        }

// Create a BarDataSet
        val barDataSet = BarDataSet(barEntries, "Condition Wise Stock").apply {
            colors = ColorTemplate.MATERIAL_COLORS.toList() // Set colors for each condition
            valueTextColor = Color.WHITE
            valueTextSize = 16f
        }

// Create BarData and set it to the chart
        val barData = BarData(barDataSet)
        horizontalBarChart.data = barData

// Customize the chart appearance
        horizontalBarChart.axisLeft.isEnabled = false // Disable left Y-axis
        horizontalBarChart.axisRight.isEnabled = false // Disable right Y-axis
        horizontalBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(conditionNames) // Set condition names
        horizontalBarChart.xAxis.granularity = 1f
        horizontalBarChart.description.isEnabled = false // Disable description
        horizontalBarChart.animateY(1000) // Add animation for horizontal bar chart

// Refresh the chart
        horizontalBarChart.invalidate() // Refresh the chart to reflect the changes
    }



    fun lineCharData(){
        val lineChart = binding.topModelWiseLineChart

        // Sample data for the line chart (could be daily stock counts, for example)
        val lineConditionNames = listOf("Gold", "Silver", "Platinum")
        val lineConditionValues = listOf(150f, 80f, 30f)

        // Create a list of Entry objects for line chart
        val lineEntries = ArrayList<Entry>()
        for (i in lineConditionValues.indices) {
            lineEntries.add(Entry(i.toFloat(), lineConditionValues[i]))
        }

        // Create a LineDataSet
        val lineDataSet = LineDataSet(lineEntries, "Condition Wise Stock").apply {
            color = Color.BLUE // Line color
            valueTextColor = Color.WHITE // Value text color
            valueTextSize = 16f // Size of value text
            lineWidth = 2f // Width of the line
            circleRadius = 4f // Size of the circles on points
            setCircleColor(Color.BLUE) // Circle color
        }

        // Create LineData and set it to the chart
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        // Customize the chart appearance
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(lineConditionNames) // Set condition names
        lineChart.xAxis.granularity = 1f
        lineChart.description.isEnabled = false // Disable description
        lineChart.animateXY(1000, 1000) // Add animation for line chart

        // Refresh the chart
        lineChart.invalidate()
    }


























    private fun initialization(view : View) {

//        stackView = view.findViewById(R.id.idStackView)
//        totalItemTextView = view.findViewById(R.id.totalItem)
//        totalSoldTextView = view.findViewById(R.id.totalSold)
//        deviceNameOptions = view.findViewById(R.id.device_name)
//        deviceModelOptions = view.findViewById(R.id.device_model)
//        submitButton = view.findViewById(R.id.button_first)
//        quantityEditText = view.findViewById(R.id.quantity)


//        stackView()
//
//
//        val database = Firebase.database
//        myDB = database.getReference("Device")
//
//
//        val languages = resources.getStringArray(R.array.programming_languages)
//        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_menu, languages)
//
//        deviceNameOptions.setAdapter(arrayAdapter)
//
//
//        val languages1 = resources.getStringArray(R.array.device_model)
//        getAllUserIds()
//        val arrayAdapter1 =
//            ArrayAdapter(requireContext(), R.layout.dropdown_menu, languages1)
//        deviceModelOptions.setAdapter(arrayAdapter1)
//
//


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

//        val brandCountCall = RetrofitInstance.getApiInterface().getItemCount()
//        brandCountCall.enqueue(object : Callback<InventoryCountResponses> {
//            override fun onResponse(
//                call: Call<InventoryCountResponses>, response: Response<InventoryCountResponses>
//            ) {
//                if (response.isSuccessful && response.body() != null) {
//
//                    list = response.body()
//                    Log.e("Failure", "Error in getting item Count : ${list?.totalBrands}")
//                    val numberList: ArrayList<StackViewList> = ArrayList()
//
//                    val temp: String = getString(
//                        R.string.totalCountPlaceHolder,
//                        list?.totalBrands.toString()
//                    )
//                    totalItemTextView.text = temp
//
//                    list?.brands?.forEach { brand ->
//                        Log.e("test", "item ${brand.name + brand.totalModels}")
//                        numberList.add(StackViewList(brand.name, brand.totalModels))
//                    }
//                    Log.e("test", "item $numberList")
//
//                    // Setting up the adapter for StackView
//                    stackViewAdapter = StackViewAdapter(
//                        context!!, R.layout.category_stocks, numberList
//                    )
//                    stackView.adapter = stackViewAdapter
//
//                }
//            }
//
//            override fun onFailure(call: Call<InventoryCountResponses>, t: Throwable) {
//                Log.e("Failure", "Error in getting item Count : ${t.message}")
//            }
//
//        })
//

    }
//
//    private fun getExampleData() {
//        val call = RetrofitInstance.getApiInterface().getExampleData()
//        call.enqueue(object : Callback<ArrayList<BrandNameResponseItem>> {
//            override fun onResponse(
//                call: Call<ArrayList<BrandNameResponseItem>>,
//                response: Response<ArrayList<BrandNameResponseItem>>
//            ) {
//                if (response.isSuccessful && response.body() != null) {
//                    // TODO: Process data
//                    Log.d("RetrofitTest", "response size ${response.body()!!.size}")
//                    Log.d("RetrofitTest", "response 1st ${response.body()!!.get(0)}")
//                } else {
//                    Log.d("RetrofitTest", "response failed 1${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ArrayList<BrandNameResponseItem>>, t: Throwable) {
//                Log.d("RetrofitTest", "response failed ${t.message}")
//            }
//        })
//    }

//
//    private fun genBarcode(inputValue: String) {
//
//        if (inputValue.isNotEmpty()) {
//            // Initializing a MultiFormatWriter to encode the input value
//            val mwriter = MultiFormatWriter()
//
//            try {
//                // Generating a barcode matrix
//                val matrix = mwriter.encode(inputValue.toString(), BarcodeFormat.CODE_128, 200, 200)
//
//                // Creating a bitmap to represent the barcode
//                val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565)
//
//                // Iterating through the matrix and set pixels in the bitmap
//                for (i in 0 until 200) {
//                    for (j in 0 until 200) {
//                        bitmap.setPixel(i, j, if (matrix[i, j]) Color.BLACK else Color.WHITE)
//                    }
//                }
//
//                // Seting the bitmap as the image resource of the ImageView
//                saveImage(bitmap)
//            } catch (e: Exception) {
//
//                Snackbar.make(myView, "Exception $e", Snackbar.LENGTH_LONG).setAction("OK") {
//                    // Handle the action here
//                    // e.g., retrying an operation
//                }.show()
//            }
//        } else {
//            // Showing an error message if the EditText is empty
//            Snackbar.make(myView, "Error in getting ID ", Snackbar.LENGTH_LONG)
//                .setAction("OK") {
//                    // Handle the action here
//                    // e.g., retrying an operation
//                }.show()
//        }
//    }
//
//    fun saveImage(bitmap: Bitmap) {
//        //Generating a file name
//        val filename = "${System.currentTimeMillis()}.jpg"
//
//        //Output stream
//        var fos: OutputStream? = null
//
//        //For devices running android >= Q
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            //getting the contentResolver
//            requireContext().contentResolver?.also { resolver ->
//
//                //Content resolver will process the contentvalues
//                val contentValues = ContentValues().apply {
//
//                    //putting file information in content values
//                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
//                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
//                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
//                }
//
//                //Inserting the contentValues to contentResolver and getting the Uri
//                val imageUri: Uri? =
//                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
//
//                //Opening an outputstream with the Uri that we got
//                fos = imageUri?.let { resolver.openOutputStream(it) }
//            }
//        } else {
//            //These for devices running on android < Q
//            //So I don't think an explanation is needed here
//            val imagesDir =
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//            val image = File(imagesDir, filename)
//            fos = FileOutputStream(image)
//        }
//
//        fos?.use {
//            //Finally writing the bitmap to the output stream that we opened
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//
//            Snackbar.make(myView, "Saved to Photos", Snackbar.LENGTH_LONG).setAction("OK") {
//                // Handle the action here
//                // e.g., retrying an operation
//            }.show()
//
//        }
//    }


}