package com.example.ringqrapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ringqrapp.R
import com.example.ringqrapp.model.DateItem

class DateSelectorAdapter(
    private var dates: List<DateItem>,
    private val onDateSelected: (DateItem) -> Unit
) : RecyclerView.Adapter<DateSelectorAdapter.DateViewHolder>() {

    private var selectedPosition = dates.indexOfFirst { it.isSelected }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(dateItem: DateItem, position: Int) {
            tvDate.text = dateItem.getDisplayDate()

            // Update appearance based on state
            when {
                dateItem.isFuture -> {
                    // Future dates - disabled
                    tvDate.setTextColor(ContextCompat.getColor(itemView.context, R.color.text_disabled))
                    tvDate.background = null
                    itemView.isClickable = false
                }
                position == selectedPosition -> {
                    // Selected date
                    tvDate.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                    tvDate.background = ContextCompat.getDrawable(itemView.context, R.drawable.date_selected_background)
                }
                dateItem.isToday -> {
                    // Today
                    tvDate.setTextColor(ContextCompat.getColor(itemView.context, R.color.date_selected))
                    tvDate.background = ContextCompat.getDrawable(itemView.context, R.drawable.date_today_background)
                }
                else -> {
                    // Normal dates
                    tvDate.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                    tvDate.background = null
                }
            }

            itemView.setOnClickListener {
                if (!dateItem.isFuture) {
                    val previousPosition = selectedPosition
                    selectedPosition = position

                    // Update selection state
                    if (previousPosition != -1 && previousPosition < dates.size) {
                        dates[previousPosition].isSelected = false
                    }
                    dateItem.isSelected = true

                    // Notify changes
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)

                    onDateSelected(dateItem)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date_selector, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(dates[position], position)
    }

    override fun getItemCount(): Int = dates.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateDates(newDates: List<DateItem>) {
        dates = newDates
        selectedPosition = dates.indexOfFirst { it.isSelected }
        notifyDataSetChanged()
    }

    fun selectDate(dateItem: DateItem) {
        val position = dates.indexOf(dateItem)
        if (position != -1) {
            val previousPosition = selectedPosition
            selectedPosition = position

            if (previousPosition != -1) {
                dates[previousPosition].isSelected = false
                notifyItemChanged(previousPosition)
            }

            dateItem.isSelected = true
            notifyItemChanged(selectedPosition)
        }
    }
}
