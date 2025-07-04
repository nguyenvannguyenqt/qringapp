package com.example.ringqrapp.model

data class TemperatureBody(
    var name:String,
    val temperature: Float,
    val timestamp: Long,
    val message: String,
    val background: Int,
    val icon:Int,
)