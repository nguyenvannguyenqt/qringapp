package com.example.ringqrapp.model
import java.util.*
data class DateItem(
    val date: Int,
    val dayOfWeek: Int, // Calendar.MONDAY, TUESDAY, etc.
    val month: Int,
    val year: Int,
    var isSelected: Boolean = false,
    var isToday: Boolean = false,
    var isFuture: Boolean = false
) {
    fun getDisplayDate(): String = date.toString()

    fun getDayOfWeekPosition(): Int {
        // Convert Calendar day of week to position (0-6, Monday = 0)
        return when (dayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }

    fun getCalendar(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, date)
        }
    }
}