package com.example.ringqrapp.constant

import com.example.ringqrapp.model.DateItem
import java.text.SimpleDateFormat
import java.util.*
object CalendarUtils {

    private val dateFormat = SimpleDateFormat("EEEE, dd 'thg' MM, yyyy", Locale("vi", "VN"))
    private val monthYearFormat = SimpleDateFormat("'thg' MM, yyyy", Locale("vi", "VN"))

    fun getCurrentDate(): Calendar = Calendar.getInstance()

    fun formatHeaderDate(calendar: Calendar): String {
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Th 2"
            Calendar.TUESDAY -> "Th 3"
            Calendar.WEDNESDAY -> "Th 4"
            Calendar.THURSDAY -> "Th 5"
            Calendar.FRIDAY -> "Th 6"
            Calendar.SATURDAY -> "Th 7"
            Calendar.SUNDAY -> "CN"
            else -> ""
        }

        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        return "$dayOfWeek, $day thg $month, $year"
    }

    fun generateMonthDates(year: Int, month: Int): List<DateItem> {
        val dates = mutableListOf<DateItem>()
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        // Set to first day of month
        calendar.set(year, month, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val dateItem = DateItem(
                date = day,
                dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK),
                month = month,
                year = year,
                isToday = isSameDay(calendar, today),
                isFuture = calendar.after(today)
            )

            dates.add(dateItem)
        }

        return dates
    }

    fun generateWeekDatesAroundDate(centerDate: Calendar, weeksToShow: Int = 4): List<DateItem> {
        val dates = mutableListOf<DateItem>()
        val calendar = centerDate.clone() as Calendar
        val today = Calendar.getInstance()

        // Go back to show previous weeks
        calendar.add(Calendar.WEEK_OF_YEAR, -weeksToShow / 2)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val totalDays = weeksToShow * 7

        for (i in 0 until totalDays) {
            val dateItem = DateItem(
                date = calendar.get(Calendar.DAY_OF_MONTH),
                dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK),
                month = calendar.get(Calendar.MONTH),
                year = calendar.get(Calendar.YEAR),
                isToday = isSameDay(calendar, today),
                isFuture = calendar.after(today)
            )

            dates.add(dateItem)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun getDatePosition(dates: List<DateItem>, targetDate: Calendar): Int {
        return dates.indexOfFirst { dateItem ->
            dateItem.year == targetDate.get(Calendar.YEAR) &&
                    dateItem.month == targetDate.get(Calendar.MONTH) &&
                    dateItem.date == targetDate.get(Calendar.DAY_OF_MONTH)
        }
    }

    fun getDayOfWeekName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> "Hai"
            Calendar.TUESDAY -> "Ba"
            Calendar.WEDNESDAY -> "Tư"
            Calendar.THURSDAY -> "Năm"
            Calendar.FRIDAY -> "Sáu"
            Calendar.SATURDAY -> "Bảy"
            Calendar.SUNDAY -> "CN"
            else -> ""
        }
    }
}