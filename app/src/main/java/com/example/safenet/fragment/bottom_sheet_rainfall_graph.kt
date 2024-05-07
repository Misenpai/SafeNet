package com.example.safenet.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safenet.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject


class bottom_sheet_rainfall_graph : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_rainfall_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barChart = view.findViewById<BarChart>(R.id.barChart)
        val predictedRainfall = arguments?.getString("predicted_rainfall")

        if (predictedRainfall != null) {
            val rainfallData = JSONObject(predictedRainfall)
            setupBarChart(barChart, rainfallData)
        }
    }

    private fun setupBarChart(barChart: BarChart, rainfallData: JSONObject) {
        // Create BarEntry objects from the predicted rainfall data
        val barEntries = mutableListOf<BarEntry>()
        val months = arrayOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

        for ((index, month) in months.withIndex()) {
            val rainfallValue = rainfallData.optDouble(month, 0.0)
            barEntries.add(BarEntry(index.toFloat(), rainfallValue.toFloat()))
        }

        // Create the BarDataSet
        val dataSet = BarDataSet(barEntries, "Rainfall by Month")
        val barData = BarData(dataSet)

        // Configure the BarChart
        barChart.data = barData
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawGridLines(true)
        barChart.axisRight.isEnabled = false

        barChart.description.isEnabled = false
        barChart.invalidate()  // Refresh the chart
    }

}