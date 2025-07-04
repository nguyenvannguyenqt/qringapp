package com.example.ringqrapp.model

data class Sleep(
    var name:String,
    val timeSleep: Long,
    val timestamp: Long,
    val message: String,
    val background: Int,
    val icon:Int,
)