package com.example.ringqrapp.model

data class WeekDay(
    val dayName: String,
    val dayNumber: Int,
    val date: String,
    var isSelected: Boolean = false
)