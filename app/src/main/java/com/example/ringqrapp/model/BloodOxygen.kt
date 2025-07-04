package com.example.ringqrapp.model

data class BloodOxygen(
    var name:String,
    val oxygenLevel: Int,
    val timestamp: Long,
    val message: String,
    val background: Int,
    val icon:Int,
)