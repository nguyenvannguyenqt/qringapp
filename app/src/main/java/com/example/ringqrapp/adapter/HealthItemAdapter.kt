package com.example.ringqrapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ringqrapp.databinding.LayoutItemCategoriesHomeBinding
import com.example.ringqrapp.model.HealthItem

class HealthItemAdapter(
    private val items: List<HealthItem>
) : RecyclerView.Adapter<HealthItemAdapter.HealthItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HealthItemViewHolder {
        val binding = LayoutItemCategoriesHomeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HealthItemViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: HealthItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class HealthItemViewHolder(private val binding: LayoutItemCategoriesHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HealthItem) {
            when (item) {
                is HealthItem.Temperature -> {
                    binding.txtNameCategories.text = item.temperature.name
                    binding.txtMessage.text = item.temperature.message
                    binding.imgIconCategories.setImageResource(item.temperature.icon)
                    binding.imgBackgroundCategories.setBackgroundResource(item.temperature.background)
                }
                is HealthItem.Oxygen -> {
                    item.oxygen.let {
                        binding.txtNameCategories.text = it.name
                        binding.txtMessage.text = it.message
                        binding.imgIconCategories.setImageResource(it.icon)
                        binding.imgBackgroundCategories.setBackgroundResource(it.background)
                    }
                }
                is HealthItem.Stress -> {
                    item.stress.let {
                        binding.txtNameCategories.text = it.name
                        binding.txtMessage.text = it.message
                        binding.imgIconCategories.setImageResource(it.icon)
                        binding.imgBackgroundCategories.setBackgroundResource(it.background)
                    }
                }
                is HealthItem.Praise -> {
                    item.praise.let {
                        binding.txtNameCategories.text = it.name
                        binding.txtMessage.text = it.message
                        binding.imgIconCategories.setImageResource(it.icon)
                        binding.imgBackgroundCategories.setBackgroundResource(it.background)
                    }
                }
                is HealthItem.Sleep -> {
                    item.sleep.let {
                        binding.txtNameCategories.text = it.name
                        binding.txtMessage.text = it.message
                        binding.imgIconCategories.setImageResource(it.icon)
                        binding.imgBackgroundCategories.setBackgroundResource(it.background)
                    }
                }
                is HealthItem.HeartRate -> {
                    item.heart_rate.let {
                        binding.txtNameCategories.text = it.name
                        binding.txtMessage.text = it.message
                        binding.imgIconCategories.setImageResource(it.icon)
                        binding.imgBackgroundCategories.setBackgroundResource(it.background)
                    }
                }
            }
        }
    }
}