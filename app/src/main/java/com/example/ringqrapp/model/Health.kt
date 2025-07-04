package com.example.ringqrapp.model

sealed class HealthItem {
    data class Temperature(val temperature : TemperatureBody) : HealthItem()
    data class Oxygen(val oxygen : BloodOxygen) : HealthItem()
    data class Stress(val stress: com.example.ringqrapp.model.Stress) : HealthItem()
    data class Praise(val praise: com.example.ringqrapp.model.Praise) : HealthItem()
    data class Sleep(val sleep: com.example.ringqrapp.model.Sleep) : HealthItem()
    data class HeartRate(val heart_rate: com.example.ringqrapp.model.HeartRate) : HealthItem()
}