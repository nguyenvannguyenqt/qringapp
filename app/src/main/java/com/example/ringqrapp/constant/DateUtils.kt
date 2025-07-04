//package com.example.ringqrapp.constant
//
//import com.example.ringqrapp.model.DateItem
//import java.text.SimpleDateFormat
//import java.util.*
//
//object DateUtils {
//
//    fun generateWeekDates(): List<DateItem> {
//        val dates = mutableListOf<DateItem>()
//        val calendar = Calendar.getInstance()
//        val dayNames = arrayOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")
//
//        // Get current week starting from Monday
//        calendar.firstDayOfWeek = Calendar.MONDAY
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
//
//        for (i in 0..6) {
//            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
//            val dayName = when (dayOfWeek) {
//                Calendar.MONDAY -> "Hai"
//                Calendar.TUESDAY -> "Ba"
//                Calendar.WEDNESDAY -> "Tư"
//                Calendar.THURSDAY -> "Năm"
//                Calendar.FRIDAY -> "Sáu"
//                Calendar.SATURDAY -> "Bảy"
//                Calendar.SUNDAY -> "CN"
//                else -> "?"
//            }
//
//            dates.add(
//                DateItem(
//                    dayName = dayName,
//                    date = calendar.get(Calendar.DAY_OF_MONTH).toString(),
//                    isSelected = i == 1 // Default select Tuesday
//                )
//            )
//
//            calendar.add(Calendar.DAY_OF_MONTH, 1)
//        }
//
//        return dates
//    }
//
//    fun formatSleepTime(hours: Int, minutes: Int): String {
//        return "$hours giờ $minutes phút"
//    }
//
//    fun formatMinutes(totalMinutes: Int): String {
//        val hours = totalMinutes / 60
//        val minutes = totalMinutes % 60
//
//        return when {
//            hours > 0 && minutes > 0 -> "$hours giờ $minutes phút"
//            hours > 0 -> "$hours giờ"
//            else -> "$minutes phút"
//        }
//    }
//}
