package com.example.ringqrapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ringqrapp.R
import com.example.ringqrapp.adapter.WeekDayAdapter
import com.example.ringqrapp.constant.ChartHelper
import com.example.ringqrapp.constant.CircularProgressHelper
import com.example.ringqrapp.constant.HealthDataManager
import com.example.ringqrapp.databinding.FragmentActivityBinding
import com.example.ringqrapp.model.HealthData
import com.example.ringqrapp.model.WeekDay
import com.github.mikephil.charting.data.BarEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ActivityFragment : Fragment() {
    private var activityBinding: FragmentActivityBinding? = null
    private val binding get() = activityBinding!!

    private lateinit var weekDayAdapter: WeekDayAdapter
    private val weekDays = mutableListOf<WeekDay>()
    private val healthDataMap = mutableMapOf<String, HealthData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (activityBinding == null) {
            activityBinding = FragmentActivityBinding.inflate(inflater, container, false)
        }
        initViews()
        setupWeekDays()
        setupRecyclerView()
        generateFakeData()

        // Chọn ngày hiện tại
        val today = getCurrentDayOfWeek()
        selectTodayByDefault(today)
        return binding.root
    }

    private fun initViews() {
        // Set current date in header
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.tvDate.text = dateFormat.format(Calendar.getInstance().time)

        // Setup click listeners for header icons
        binding.ivShare.setOnClickListener {
            // Handle share action
            shareHealthData()
        }

        binding.ivSync.setOnClickListener {
            // Handle sync action
            syncHealthData()
        }

        // Setup activity points click
        binding.activityPointsLayout.setOnClickListener {
            // Navigate to activity details
        }
    }

    private fun setupWeekDays() {
        weekDays.clear()
        val calendar = Calendar.getInstance()
        val dayNames = arrayOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Lấy ngày đầu tuần (Chủ nhật)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        for (i in 0..6) {
            val dayName = dayNames[i]
            val dayNumber = calendar.get(Calendar.DAY_OF_MONTH)
            val date = dateFormat.format(calendar.time)

            weekDays.add(WeekDay(dayName, dayNumber, date))
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }


    private fun setupRecyclerView() {
        weekDayAdapter = WeekDayAdapter(weekDays) { selectedDay ->
            onDaySelected(selectedDay)
        }

        binding.rvWeekDays.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = weekDayAdapter
        }
    }

    private fun generateFakeData() {
        weekDays.forEachIndexed { index, weekDay ->
            val healthData = HealthDataManager.getFakeHealthData(index)
            healthDataMap[weekDay.date] = healthData
        }
    }

    private fun onDaySelected(selectedDay: WeekDay) {
        // Cập nhật ngày hiển thị
        binding.tvDate.text = selectedDay.date

        // Lấy dữ liệu cho ngày được chọn
        val healthData = healthDataMap[selectedDay.date] ?: return

        // Cập nhật progress indicators
        updateProgressIndicators(healthData)

        // Cập nhật charts
        updateCharts(healthData)

        // Cập nhật activity points
        updateActivityPoints(healthData)
    }

    private fun updateProgressIndicators(healthData: HealthData) {
        // Steps Progress
        CircularProgressHelper.setupCircularProgress(
            binding.stepsProgress.root,
            binding.stepsProgress.tvProgressValue,
            binding.stepsProgress.tvProgressLabel,
            binding.stepsProgress.tvProgressTarget,
            healthData.steps.current.toString(),
            "Tổng số bước",
            "/${healthData.steps.target}",
            healthData.steps.progress,
            R.color.progress_blue
        )

        // Distance Progress
        CircularProgressHelper.setupCircularProgress(
            binding.distanceProgress.root,
            binding.distanceProgress.tvProgressValue,
            binding.distanceProgress.tvProgressLabel,
            binding.distanceProgress.tvProgressTarget,
            String.format("%.2f", healthData.distance.current),
            "Tổng số dặm",
            "/${healthData.distance.target}km",
            healthData.distance.progress,
            R.color.progress_green
        )

        // Calories Progress
        CircularProgressHelper.setupCircularProgress(
            binding.caloriesProgress.root,
            binding.caloriesProgress.tvProgressValue,
            binding.caloriesProgress.tvProgressLabel,
            binding.caloriesProgress.tvProgressTarget,
            healthData.calories.current.toString(),
            "Tổng calo",
            "/${healthData.calories.target}kcal",
            healthData.calories.progress,
            R.color.progress_red
        )
    }

    private fun updateCharts(healthData: HealthData) {
        // Steps Chart
        val stepsEntries = healthData.steps.hourlyData.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.toFloat())
        }
        ChartHelper.setupBarChart(
            requireContext(),
            binding.stepsChartSection.barChart,
            stepsEntries,
            R.color.progress_blue,
            "Steps"
        )
        binding.stepsChartSection.tvChartTitle.text = "Số bước"
        binding.stepsChartSection.tvChartValue.text = healthData.steps.current.toString()
        binding.stepsChartSection.tvChartUnit.text = "các bước"
        binding.stepsChartSection.tvChartPercentage.text =
            "${(healthData.steps.progress * 0.1f).toInt()}%"

        // Distance Chart
        val distanceEntries = healthData.distance.hourlyData.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }
        ChartHelper.setupBarChart(
            requireContext(),
            binding.distanceChartSection.barChart,
            distanceEntries,
            R.color.progress_green,
            "Distance"
        )
        binding.distanceChartSection.tvChartTitle.text = "Số dặm"
        binding.distanceChartSection.tvChartValue.text =
            String.format("%.2f", healthData.distance.current)
        binding.distanceChartSection.tvChartUnit.text = "km"
        binding.distanceChartSection.tvChartPercentage.text =
            String.format("%.1fkm", healthData.distance.current * 0.5f)

        // Calories Chart
        val caloriesEntries = healthData.calories.hourlyData.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.toFloat())
        }
        ChartHelper.setupBarChart(
            requireContext(),
            binding.caloriesChartSection.barChart,
            caloriesEntries,
            R.color.progress_red,
            "Calories"
        )
        binding.caloriesChartSection.tvChartTitle.text = "Calo"
        binding.caloriesChartSection.tvChartValue.text = healthData.calories.current.toString()
        binding.caloriesChartSection.tvChartUnit.text = "kcal"
        binding.caloriesChartSection.tvChartPercentage.text =
            "${(healthData.calories.progress * 0.01f).toInt()}%"
    }

    private fun updateActivityPoints(healthData: HealthData) {
        // Tính toán activity points dựa trên dữ liệu
        val activityPoints = calculateActivityPoints(healthData)

        // Cập nhật UI (giả sử có TextView trong layout)
        // binding.tvActivityPoints.text = activityPoints.toString()
    }

    private fun calculateActivityPoints(healthData: HealthData): Int {
        val stepsPoints = (healthData.steps.current / 1000) * 10
        val distancePoints = (healthData.distance.current * 20).toInt()
        val caloriesPoints = healthData.calories.current / 50

        return stepsPoints + distancePoints + caloriesPoints
    }

    private fun getCurrentDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> 0
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            else -> 0
        }
    }

    private fun selectTodayByDefault(todayIndex: Int) {
        if (todayIndex < weekDays.size) {
            weekDays[todayIndex].isSelected = true
            weekDayAdapter.notifyItemChanged(todayIndex)
            onDaySelected(weekDays[todayIndex])

            // Scroll to today
            binding.rvWeekDays.scrollToPosition(todayIndex)
        }
    }

    private fun shareHealthData() {
        // Implement share functionality
        val selectedDay = weekDays.find { it.isSelected }
        val healthData = selectedDay?.let { healthDataMap[it.date] }

        healthData?.let {
            val shareText = buildString {
                append("Dữ liệu sức khỏe ngày ${selectedDay.date}\n")
                append("Bước chân: ${it.steps.current}/${it.steps.target}\n")
                append(
                    "Quãng đường: ${
                        String.format(
                            "%.2f",
                            it.distance.current
                        )
                    }/${it.distance.target}km\n"
                )
                append("Calo: ${it.calories.current}/${it.calories.target}kcal")
            }

            // Create share intent
            val shareIntent = android.content.Intent().apply {
                action = android.content.Intent.ACTION_SEND
                type = "text/plain"
                putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            }
            startActivity(
                android.content.Intent.createChooser(
                    shareIntent,
                    "Chia sẻ dữ liệu sức khỏe"
                )
            )
        }
    }

    private fun syncHealthData() {
        // Implement sync functionality
        // Show loading indicator
        // Call API to sync data
        // Update UI with new data

        // For now, just regenerate fake data
        generateFakeData()
        val selectedDay = weekDays.find { it.isSelected }
        selectedDay?.let { onDaySelected(it) }

        // Show success message
        android.widget.Toast.makeText(
            requireContext(),
            "Đồng bộ dữ liệu thành công",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    // Update charts with new data
    // setupBarChart(binding.stepsChartSection.barChart, stepsEntries)
    // setupBarChart(binding.distanceChartSection.barChart, distanceEntries)
    // setupBarChart(binding.caloriesChartSection.barChart, caloriesEntries)
}