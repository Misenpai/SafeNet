package com.example.safenet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.safenet.R
import com.example.safenet.RainfallDataListener
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class bottom_sheet_rainfall_graph : BottomSheetDialogFragment(),RainfallDataListener {
    private var rainfallData: Map<String, Float>? = null

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
        rainfallData?.let { setupBarChart(barChart, it) }
    }


    private fun setupBarChart(barChart: BarChart, rainfallData: Map<String, Float>) {
        val months = rainfallData.keys.toTypedArray()
        val entries = ArrayList<BarEntry>()

        for ((index, month) in months.withIndex()) {
            entries.add(BarEntry(index.toFloat(), rainfallData[month] ?: 0f))
        }

        val barDataSet = BarDataSet(entries, "Rainfall")
        val barData = BarData(barDataSet)

        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(months)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.invalidate()
    }

    fun setRainfallData(rainfallData: Map<String, Float>) {
        this.rainfallData = rainfallData
    }

    override fun onRainfallDataReceived(rainfallData: Map<String, Float>) {
        setRainfallData(rainfallData)
    }

}