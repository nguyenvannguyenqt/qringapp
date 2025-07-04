package com.example.ringqrapp.constant

import android.graphics.Color
import android.graphics.DashPathEffect
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.sin

object ChartUtils {

    fun setupHeartRateChart(chart: LineChart) {
        setupBaseChart(chart)

        // Add limit lines for heart rate
        val limitLines = listOf(160f, 120f, 80f, 40f)
        addLimitLines(chart, limitLines)

        // Generate sample heart rate data
        val entries = generateHeartRateData()
        val dataSet = createLineDataSet(entries, "Nhịp tim", Color.parseColor("#FF6B6B"))

        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    fun setupSpO2Chart(chart: LineChart) {
        setupBaseChart(chart)

        // Add limit lines for SpO2
        val limitLines = listOf(100f, 95f, 90f, 85f)
        addLimitLines(chart, limitLines)

        // Generate sample SpO2 data
        val entries = generateSpO2Data()
        val dataSet = createLineDataSet(entries, "SpO2", Color.parseColor("#4ECDC4"))

        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    fun setupHRVChart(chart: LineChart) {
        setupBaseChart(chart)

        // Add limit lines for HRV
        val limitLines = listOf(178f, 119f, 59f, 30f)
        addLimitLines(chart, limitLines)

        // Generate sample HRV data
        val entries = generateHRVData()
        val dataSet = createLineDataSet(entries, "HRV", Color.parseColor("#45B7D1"))

        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    private fun setupBaseChart(chart: LineChart) {
        chart.apply {
            // Chart styling
            setBackgroundColor(Color.TRANSPARENT)
            setGridBackgroundColor(Color.TRANSPARENT)
            setDrawGridBackground(false)
            setDrawBorders(false)

            // Disable interactions we don't want
            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)
            setPinchZoom(false)

            // Hide description
            description.isEnabled = false

            // Hide legend
            legend.isEnabled = false

            // Configure X axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
                textColor = Color.WHITE
            }

            // Configure left Y axis
            axisLeft.apply {
                setDrawGridLines(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
                textColor = Color.WHITE
            }

            // Disable right Y axis
            axisRight.isEnabled = false

            // Animation
            animateX(1000)
        }
    }

    private fun addLimitLines(chart: LineChart, values: List<Float>) {
        values.forEach { value ->
            val limitLine = LimitLine(value, value.toInt().toString()).apply {
                lineColor = Color.parseColor("#8A9BB8")
                lineWidth = 1f
                //pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                textColor = Color.parseColor("#8A9BB8")
                textSize = 10f
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            }
            chart.axisLeft.addLimitLine(limitLine)
        }
    }

    private fun createLineDataSet(entries: List<Entry>, label: String, color: Int): LineDataSet {
        return LineDataSet(entries, label).apply {
            this.color = color
            setCircleColor(color)
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(false)
            setDrawValues(false)
            setDrawFilled(true)
            fillColor = color
            fillAlpha = 30
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.2f
        }
    }

    private fun generateHeartRateData(): List<Entry> {
        val entries = mutableListOf<Entry>()
        val baseValue = 58f

        for (i in 0..29) {
            val variation = sin(i * 0.3) * 8 + sin(i * 0.1) * 4
            val value = (baseValue + variation).coerceIn(45.0, 85.0)
            entries.add(Entry(i.toFloat(), value.toFloat()))
        }
        return entries
    }

    private fun generateSpO2Data(): List<Entry> {
        val entries = mutableListOf<Entry>()
        val baseValue = 97f

        for (i in 0..29) {
            val variation = sin(i * 0.2) * 1.5f + sin(i * 0.05) * 0.8f
            val value = (baseValue + variation).coerceIn(94.0, 100.0)
            entries.add(Entry(i.toFloat(), value.toFloat()))
        }
        return entries
    }

    private fun generateHRVData(): List<Entry> {
        val entries = mutableListOf<Entry>()
        val baseValue = 65f

        for (i in 0..29) {
            val variation = sin(i * 0.4) * 12 + sin(i * 0.15) * 6
            val value = (baseValue + variation).coerceIn(35.0, 95.0)
            entries.add(Entry(i.toFloat(), value.toFloat()))
        }
        return entries
    }

    fun updateChartData(chart: LineChart, newData: List<Float>, color: Int) {
        val entries = newData.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        val dataSet = createLineDataSet(entries, "", color)
        chart.data = LineData(dataSet)
        chart.notifyDataSetChanged()
        chart.invalidate()
    }
}
