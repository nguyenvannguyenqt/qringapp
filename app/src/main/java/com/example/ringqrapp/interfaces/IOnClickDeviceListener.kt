package com.example.ringqrapp.interfaces

import com.example.ringqrapp.model.InformationRingDevice

interface IOnClickDeviceListener {
    fun onClickDevice(device: InformationRingDevice)
    fun onDeleteDevice(position: Int, device: InformationRingDevice)
}