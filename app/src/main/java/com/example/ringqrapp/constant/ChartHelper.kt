package com.example.ringqrapp.constant

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.ringqrapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

object ChartHelper {

    fun setupBarChart(
        context: Context,
        barChart: BarChart,
        data: List<BarEntry>,
        color: Int,
        label: String
    ) {
        val dataSet = BarDataSet(data, label).apply {
            this.color = ContextCompat.getColor(context, color)
            setDrawValues(false)
            isHighlightEnabled = false
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        barChart.apply {
            this.data = barData
            description.isEnabled = false
            legend.isEnabled = false

            // Tùy chỉnh trục X
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = ContextCompat.getColor(context, R.color.white_70)
                textSize = 8f
                granularity = 4f
                labelCount = 6
                valueFormatter = IndexAxisValueFormatter(getTimeLabels())
                setLabelCount(6, true)
            }

            // Tùy chỉnh trục Y trái
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
                axisMinimum = 0f
            }

            // Ẩn trục Y phải
            axisRight.isEnabled = false

            // Tùy chỉnh khác
            setTouchEnabled(false)
            setScaleEnabled(false)
            setPinchZoom(false)
            setDrawBorders(false)

            // Animation
            animateY(1000)

            // Refresh chart
            invalidate()
        }
    }

    private fun getTimeLabels(): Array<String> {
        return arrayOf("00:00", "04:00", "08:00", "12:00", "16:00", "20:00", "24:00")
    }
}