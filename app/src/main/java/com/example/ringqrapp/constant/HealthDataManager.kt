package com.example.ringqrapp.constant

import com.example.ringqrapp.model.CaloriesData
import com.example.ringqrapp.model.DistanceData
import com.example.ringqrapp.model.HealthData
import com.example.ringqrapp.model.StepsData
import kotlin.random.Random

object HealthDataManager {

    fun getFakeHealthData(dayOfWeek: Int): HealthData {
        return HealthData(
            steps = generateFakeStepsData(dayOfWeek),
            distance = generateFakeDistanceData(dayOfWeek),
            calories = generateFakeCaloriesData(dayOfWeek)
        )
    }

    private fun generateFakeStepsData(dayOfWeek: Int): StepsData {
        val baseSteps = when (dayOfWeek) {
            0, 6 -> Random.nextInt(3000, 6000) // Weekend - less active
            else -> Random.nextInt(7000, 12000) // Weekday - more active
        }

        val hourlyData = mutableListOf<Int>()
        for (hour in 0..23) {
            val steps = when (hour) {
                in 0..5 -> 0 // Sleeping
                in 6..8 -> Random.nextInt(200, 500) // Morning
                in 9..11 -> Random.nextInt(300, 800) // Morning work
                in 12..13 -> Random.nextInt(400, 600) // Lunch break
                in 14..17 -> Random.nextInt(200, 700) // Afternoon work
                in 18..20 -> Random.nextInt(500, 1000) // Evening activity
                in 21..23 -> Random.nextInt(50, 200) // Evening wind down
                else -> 0
            }
            hourlyData.add(steps)
        }

        return StepsData(
            current = baseSteps,
            target = 10000,
            hourlyData = hourlyData
        )
    }

    private fun generateFakeDistanceData(dayOfWeek: Int): DistanceData {
        val baseDistance = when (dayOfWeek) {
            0, 6 -> Random.nextFloat() * 3f + 1f // Weekend
            else -> Random.nextFloat() * 4f + 2f // Weekday
        }

        val hourlyData = mutableListOf<Float>()
        for (hour in 0..23) {
            val distance = when (hour) {
                in 0..5 -> 0f
                in 6..8 -> Random.nextFloat() * 0.5f
                in 9..11 -> Random.nextFloat() * 0.8f
                in 12..13 -> Random.nextFloat() * 0.3f
                in 14..17 -> Random.nextFloat() * 0.6f
                in 18..20 -> Random.nextFloat() * 1.2f
                in 21..23 -> Random.nextFloat() * 0.2f
                else -> 0f
            }
            hourlyData.add(distance)
        }

        return DistanceData(
            current = baseDistance,
            target = 5f,
            hourlyData = hourlyData
        )
    }

    private fun generateFakeCaloriesData(dayOfWeek: Int): CaloriesData {
        val baseCalories = when (dayOfWeek) {
            0, 6 -> Random.nextInt(150, 400) // Weekend
            else -> Random.nextInt(300, 600) // Weekday
        }

        val hourlyData = mutableListOf<Int>()
        for (hour in 0..23) {
            val calories = when (hour) {
                in 0..5 -> 0
                in 6..8 -> Random.nextInt(10, 30)
                in 9..11 -> Random.nextInt(15, 40)
                in 12..13 -> Random.nextInt(20, 35)
                in 14..17 -> Random.nextInt(10, 35)
                in 18..20 -> Random.nextInt(25, 50)
                in 21..23 -> Random.nextInt(5, 15)
                else -> 0
            }
            hourlyData.add(calories)
        }

        return CaloriesData(
            current = baseCalories,
            target = 2000,
            hourlyData = hourlyData
        )
    }
}