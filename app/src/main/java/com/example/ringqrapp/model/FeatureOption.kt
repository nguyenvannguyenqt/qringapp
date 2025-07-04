package com.example.ringqrapp.model

import android.graphics.drawable.Drawable

data class FeatureOption(
    val iconRes: Int,
    val title: String,
    val idBackgroundIcon: Drawable, //ContextCompat.getDrawable(this, R.drawable.bg_background_circle_blue_light)
    val type: OptionType,
    var value: String? = null,
    val route: String? = null,
    val isChecked: Boolean = false

)
enum class OptionType {
    NAVIGATION, // Mũi tên >
    SWITCH,     // Toggle
    VALUE       // Có giá trị bên phải như "Mét"
}