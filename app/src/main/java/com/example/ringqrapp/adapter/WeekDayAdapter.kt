package com.example.ringqrapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ringqrapp.R
import com.example.ringqrapp.databinding.ItemWeekDayBinding
import com.example.ringqrapp.model.WeekDay

class WeekDayAdapter(
    private val weekDays: List<WeekDay>,
    private val onDaySelected: (WeekDay) -> Unit
) : RecyclerView.Adapter<WeekDayAdapter.WeekDayViewHolder>() {

    private var selectedPosition = 0

    init {
        // Set first day as selected by default
        if (weekDays.isNotEmpty()) {
            weekDays[0].isSelected = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekDayViewHolder {
        val binding = ItemWeekDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WeekDayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekDayViewHolder, position: Int) {
        holder.bind(weekDays[position], position)
    }

    override fun getItemCount(): Int = weekDays.size

    inner class WeekDayViewHolder(
        private val binding: ItemWeekDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(weekDay: WeekDay, position: Int) {
            binding.apply {
                tvDayName.text = weekDay.dayName
                tvDayNumber.text = weekDay.dayNumber.toString()

                // Update selection state
                updateSelectionState(weekDay.isSelected)

                // Handle click
                root.setOnClickListener {
                    if (selectedPosition != position) {
                        // Update previous selected item
                        weekDays[selectedPosition].isSelected = false
                        notifyItemChanged(selectedPosition)

                        // Update new selected item
                        selectedPosition = position
                        weekDay.isSelected = true
                        notifyItemChanged(position)

                        // Notify callback
                        onDaySelected(weekDay)
                    }
                }
            }
        }

        private fun updateSelectionState(isSelected: Boolean) {
            binding.apply {
                if (isSelected) {
                    // Selected state - red background
                    dayBackground.setBackgroundResource(R.drawable.day_selector)
                    tvDayNumber.setTextColor(
                        ContextCompat.getColor(root.context, R.color.white)
                    )
                } else {
                    // Default state - transparent background with border
                    dayBackground.setBackgroundResource(R.drawable.day_selector)
                    tvDayNumber.setTextColor(
                        ContextCompat.getColor(root.context, R.color.white_70)
                    )
                }
            }
        }
    }
}