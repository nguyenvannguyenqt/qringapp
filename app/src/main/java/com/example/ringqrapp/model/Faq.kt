package com.example.ringqrapp.model

data class Faq(
    val question:String,
    val answer : String,
    var isExpanded: Boolean = false
)

