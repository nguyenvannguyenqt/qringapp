package com.example.ringqrapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ringqrapp.databinding.ItemLayoutSaveDeviceBinding
import com.example.ringqrapp.interfaces.IOnClickDeviceListener
import com.example.ringqrapp.model.InformationRingDevice

class DeviceSaveAdapter(
    private val mListDeviceSave : MutableList<InformationRingDevice>?,
    private val iOnClickDevice : IOnClickDeviceListener
)
    : RecyclerView.Adapter<DeviceSaveAdapter.DeviceSveViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceSveViewHolder {
        val binding = ItemLayoutSaveDeviceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DeviceSveViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mListDeviceSave?.size ?: 0
    }

    override fun onBindViewHolder(holder: DeviceSveViewHolder, position: Int) {
        val info = mListDeviceSave?.get(position)
        if (info != null) {
            holder.bind(info)
            holder.binding.btnDetailDevice.setOnClickListener {
                iOnClickDevice.onClickDevice(info)
            }
            holder.binding.btnDeleteDevice.setOnClickListener {
                iOnClickDevice.onDeleteDevice(position, info)
                //                mListDeviceSave?.removeAt(position)
//                notifyItemRemoved(position)
//                notifyItemRangeChanged(position, itemCount - position)
            }
        }
    }

    inner class DeviceSveViewHolder(val binding : ItemLayoutSaveDeviceBinding)
        : RecyclerView.ViewHolder(binding.root) {
        // Define your ViewHolder properties and methods here
        @SuppressLint("SetTextI18n")
        fun bind(info : InformationRingDevice) {
            binding.txtDeviceName.text = info.ringDevice!!.name
            binding.txtDeviceAddress.text = info.ringDevice.address
            binding.txtDeviceRssi.text = "RSSI: ${info.ringDevice.rssi} dBm"
            binding.txtBatteryValue.text = "Battery Level: ${info.battery}%"
            binding.txtUpdate.text = "Last Update: ${info.valueUpdate}"
        }
    }
}