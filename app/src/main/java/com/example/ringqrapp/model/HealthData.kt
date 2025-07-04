package com.example.ringqrapp.model

data class HealthData(
    val steps: StepsData,
    val distance: DistanceData,
    val calories: CaloriesData
)

data class StepsData(
    val current: Int,
    val target: Int,
    val hourlyData: List<Int>
) {
    val progress: Int
        get() = if (target > 0) (current * 100 / target) else 0
}

data class DistanceData(
    val current: Float,
    val target: Float,
    val hourlyData: List<Float>
) {
    val progress: Int
        get() = if (target > 0) ((current * 100) / target).toInt() else 0
}

data class CaloriesData(
    val current: Int,
    val target: Int,
    val hourlyData: List<Int>
) {
    val progress: Int
        get() = if (target > 0) (current * 100 / target) else 0
}