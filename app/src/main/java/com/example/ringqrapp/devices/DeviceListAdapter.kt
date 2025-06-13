package com.example.ringqrapp.devices

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.example.ringqrapp.R
import com.example.ringqrapp.model.RingDevice

class DeviceListAdapter(context: Context, data: MutableList<RingDevice>) :
    BaseQuickAdapter<RingDevice, BaseViewHolder>(R.layout.recycleview_item_device, data) {

    init {
        setDiffCallback(DeviceDiffCallback())
    }

    override fun convert(holder: BaseViewHolder, item: RingDevice) {
        val deviceName = item.name.takeIf { it.isNotEmpty() } ?: "Unknown Device"
        holder.setText(R.id.rcv_device_name, deviceName)
        holder.setText(R.id.rcv_device_address, item.address)
        holder.setText(R.id.rcv_device_rssi, "RSSI: ${item.rssi} dBm") //cường độ tín hiệu (RSSI) rõ ràng, giúp người dùng dễ chọn thiết bị có tín hiệu mạnh nhất.
    }

    private class DeviceDiffCallback : DiffUtil.ItemCallback<RingDevice>() {
        override fun areItemsTheSame(oldItem: RingDevice, newItem: RingDevice): Boolean {
            return oldItem.address == newItem.address
        }

        override fun areContentsTheSame(oldItem: RingDevice, newItem: RingDevice): Boolean {
            return oldItem == newItem
        }
    }
} 