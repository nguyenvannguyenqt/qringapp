package com.example.ringqrapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ringqrapp.databinding.LayoutItemFeatureOptionBinding
import com.example.ringqrapp.model.FeatureOption
import com.example.ringqrapp.model.OptionType
import com.example.ringqrapp.interfaces.IFeatureOptionClickListener

class FeatureOptionAdapter(
    private val listFeatureOptions: List<FeatureOption>,
    private val listener: IFeatureOptionClickListener
) : RecyclerView.Adapter<FeatureOptionAdapter.FeatureOptionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureOptionViewHolder {
        val binding = LayoutItemFeatureOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FeatureOptionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFeatureOptions.size
    }

    override fun onBindViewHolder(holder: FeatureOptionViewHolder, position: Int) {
        val featureOption = listFeatureOptions[position]
        holder.bind(featureOption)
        when (featureOption.type) {
            OptionType.NAVIGATION -> {
                holder.binding.imgNext.visibility = View.VISIBLE
                holder.binding.imgNext.setOnClickListener { listener.onNextClick(featureOption, position = position) }
            }
            OptionType.VALUE -> {
                holder.binding.txtValue.visibility = View.VISIBLE
                holder.binding.imgNext.visibility = View.VISIBLE
                holder.binding.imgNext.setOnClickListener { listener.onNextClick(featureOption, position = position) }
                //binding.txtValue.text = featureOption.value
            }
            OptionType.SWITCH -> {
                holder.binding.switchToggle.visibility = View.VISIBLE
                holder.binding.switchToggle.setOnCheckedChangeListener { _, isChecked ->
                    listener.onSwitchToggle(featureOption, isChecked)
                }
            }
        }
    }

    inner class FeatureOptionViewHolder( val binding: LayoutItemFeatureOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(featureOption: FeatureOption) {
            binding.imgIcon.setImageResource(featureOption.iconRes)
            binding.imgIcon.background = featureOption.idBackgroundIcon
            binding.txtTitle.text = featureOption.title
            binding.txtValue.text = featureOption.value

            // Reset visibility
            binding.txtValue.visibility = View.GONE
            binding.switchToggle.visibility = View.GONE
            binding.imgNext.visibility = View.GONE
        }
    }
}