package com.isar.imagine.dashboard

import DashboardViewmodel
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.firebase.database.DatabaseReference
import com.isar.imagine.R
import com.isar.imagine.dashboard.models.BarChartModel
import com.isar.imagine.dashboard.models.PieChartModel
import com.isar.imagine.databinding.FragmentDashboard2Binding
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results


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

    private fun onbserveData(){
        viewModel.brands.observe(viewLifecycleOwner){
            when(it){
                is Results.Loading ->{
                  CustomProgressBar.show(requireContext(),"Loading Model ...")
                }
                is Results.Success ->{
                    CustomProgressBar.dismiss()
                    barChart(it.data!!)
                    calculateTotalStock(it.data.brandQuantity.values)

                }
                is Results.Error ->{
                    CustomProgressBar.dismiss()
                    calculateTotalStock(emptyList())

                }

            }
        }
        viewModel.conditionWise.observe(viewLifecycleOwner){
            when(it){
                is Results.Loading ->{
                    CustomProgressBar.show(requireContext(),"Loading Model ...")
                }
                is Results.Success ->{
                    CustomProgressBar.dismiss()
                    pieChartData(it.data!!)
                }
                is Results.Error ->{
                    CustomProgressBar.dismiss()

                }

            }
        }

    }

    private fun calculateTotalStock(quantity: Collection<Long>) {
        if (quantity.isEmpty()){
            binding.totalSum.text = "No data available"
            return
        }
        val total =  quantity.sum()
        binding.totalSum.text = "Total item : $total"
    }


    private fun  barChart(data : BarChartModel){
        // Assuming you're using MPAndroidChart
        val barChart1 = binding.salesBarChart
        val barChart = binding.modelWiseStockBarChart


        val list = ArrayList<BarEntry>()
        for ((index,entry) in data.brandQuantity.entries.withIndex()) {
            list.add(BarEntry(index.toFloat(), entry.value.toFloat(),entry.key))
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
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(data.brandQuantity.keys) // Set model names on X-axis
        barChart.xAxis.granularity = 1f
        barChart.axisRight.isEnabled = false
        barChart.animateY(1000)

        // Refresh the chart
        barChart.invalidate()
        barChart1.invalidate()


    }


    private fun pieChartData(data : PieChartModel){

        val pieChart1 = binding.conditionWiseSalesPieChart
        val pieChart = binding.conditionWiseStockPieChart



        // Create a list of PieEntry objects
        val pieEntries =  data.map.map { (t, u) ->
            PieEntry(u.toFloat(),t,u)
        }




        // Create a PieDataSet
        val dataSet = PieDataSet(pieEntries, "Condition Wise Stock").apply {
            colors = listOf(Color.YELLOW, Color.LTGRAY, Color.DKGRAY) // Set colors for each condition
            valueTextColor = Color.BLACK
            valueTextSize = 16f
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























}