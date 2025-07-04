package com.example.ringqrapp.model

data class HeartRate(
    var name:String,
    val heart_rate: Int,
    val timestamp: Long,
    val message: String,
    val background: Int,
    val icon:Int,
)