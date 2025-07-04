package com.example.ringqrapp.activity.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class SimpleChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dashPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var chartType = ChartType.HEART_RATE
    private var gridLines = listOf<Int>()
    private var currentValue = 0

    enum class ChartType {
        HEART_RATE, SPO2, HRV
    }

    init {
        paint.color = Color.WHITE
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE

        textPaint.color = Color.WHITE
        textPaint.textSize = 24f
        textPaint.textAlign = Paint.Align.RIGHT

        dashPaint.color = Color.WHITE
        dashPaint.strokeWidth = 1f
        dashPaint.style = Paint.Style.STROKE
        dashPaint.pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)

        // Default to heart rate
        setChartData(ChartType.HEART_RATE, listOf(160, 120, 80, 40), 58)
    }

    fun setChartData(type: ChartType, lines: List<Int>, value: Int) {
        chartType = type
        gridLines = lines
        currentValue = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val padding = 10f

        // Draw grid lines
        gridLines.forEachIndexed { index, value ->
            val y = padding + (height - 2 * padding) * index / (gridLines.size - 1)

            // Draw dashed line
            canvas.drawLine(padding, y, width - padding, y, dashPaint)

            // Draw value text
            canvas.drawText(value.toString(), width - padding - 10f, y + 8f, textPaint)
        }
    }
}
