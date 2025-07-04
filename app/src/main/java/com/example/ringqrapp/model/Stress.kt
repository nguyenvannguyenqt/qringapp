package com.example.ringqrapp.model

data class Stress(
    val stressScore: Int,
    val timestamp: Long,
    var name:String,
    val message: String,
    val background: Int,
    val icon:Int,
)