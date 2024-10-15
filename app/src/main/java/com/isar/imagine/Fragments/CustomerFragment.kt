package com.isar.imagine.Fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.isar.imagine.R
import com.isar.imagine.databinding.FragmentCustomerBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CustomerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CustomerFragment : Fragment() {

    private lateinit var binding : FragmentCustomerBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCustomerBinding.inflate(inflater,container,false)
        radarChart()
        pieChartDataForTopCustomers()
        return binding.root
    }
    fun pieChartData(){

        val pieChart = binding.conditionWiseSalesPieChart


    }

    private fun pieChartDataForTopCustomers() {
        val pieChart = binding.conditionWiseSalesPieChart

        // Sample data for phone conditions
        val conditionNames = listOf("Gold", "Silver", "Platinum")
        val conditionValues = listOf(150f, 80f, 30f) // Replace with actual stock numbers

        // Create a list of PieEntry objects
        val pieEntries = java.util.ArrayList<PieEntry>()
        for (i in conditionValues.indices) {
            pieEntries.add(PieEntry(conditionValues[i], conditionNames[i]))
        }

        // Create a PieDataSet
        val dataSet = PieDataSet(pieEntries, "Condition Wise Top Customers").apply {
            colors = listOf(Color.YELLOW, Color.LTGRAY, Color.DKGRAY) // Set colors for each condition
            valueTextColor = Color.BLACK // Color of value text
            valueTextSize = 16f // Size of value text
        }

        // Create PieData and set it to the chart
        val pieData = PieData(dataSet)
        pieChart.data = pieData

        // Customize the chart appearance
        pieChart.setUsePercentValues(true) // Show values in percentages
        pieChart.description.isEnabled = false // Disable the description
        pieChart.legend.isEnabled = true // Enable legend
        pieChart.animateY(1000) // Add animation for pie chart

        // Refresh the chart
        pieChart.invalidate() // Refresh the chart to reflect the changes

    }


    private fun radarChart() {
        // Assuming you're using MPAndroidChart
        val radarChart = binding.salesRadarChart

        // Sample data for geographical regions and their sales
        val regionNames = listOf("North America", "Europe", "Asia", "South America", "Africa")
        val salesValues = listOf(12f, 15f, 10f, 50f, 75f)

        val entries = ArrayList<RadarEntry>()
        for (i in salesValues.indices) {
            entries.add(RadarEntry(salesValues[i]))
        }

        val dataSet = RadarDataSet(entries, "Sales Distribution").apply {
            color = R.color.ColorPrimary // Set the line color
            fillColor = R.color.ColorPrimary // Set fill color for the area
            lineWidth = 2f // Width of the line
            valueTextColor = R.color.white // Color of value text on points
            valueTextSize = 16f // Size of value text on points
            setDrawFilled(true) // Fill the area under the line
        }

        // Create RadarData object
        val radarData = RadarData(dataSet).apply {
            setValueFormatter(PercentFormatter()) // Optional: set value formatter if needed
        }

        // Set the data to the chart
        radarChart.data = radarData

        // Customize the chart appearance
        radarChart.description.isEnabled = false // Disable the description
        radarChart.legend.isEnabled = true // Show legend
        radarChart.webLineWidth = 1f // Line width of the web
        radarChart.webColor = R.color.reticle_ripple // Color of the web
        radarChart.webColor = R.color.ColorPrimary // Fill color of the web area
        radarChart.animateXY(1000, 1000) // Add animation for radar chart

        // Set region names on the axes
        radarChart.xAxis.valueFormatter = IndexAxisValueFormatter(regionNames)

        // Refresh the chart
        radarChart.invalidate() // Refresh the chart to reflect the changes
    }

}