package com.example.ringqrapp.interfaces

import android.app.Activity
import com.example.ringqrapp.model.ConnectionState
import com.example.ringqrapp.model.RingDevice
import kotlinx.coroutines.flow.StateFlow

interface IRingManager {
    val connectionState: StateFlow<ConnectionState>
    fun startScan(activity: Activity,callback: (RingDevice?) -> Unit)
    fun stopScan()
    fun connect(device: RingDevice)
    fun disconnect()
    fun updateConnectionState(connected: Boolean)
    fun startHeartRateMeasurement(callback: (Int) -> Unit)
    fun startBloodOxygenMeasurement(callback: (Int) -> Unit)
    fun startHrvMeasurement(callback: (Int) -> Unit)
    fun startTemperatureMeasurement(callback: (Float) -> Unit)
    fun startSleepSync(callback: (String) -> Unit)
}