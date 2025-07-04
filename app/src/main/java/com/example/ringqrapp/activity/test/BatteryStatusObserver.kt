package com.example.ringqrapp.activity.test

import android.content.Context
import android.util.Log
import com.oudmon.ble.base.bluetooth.BleOperateManager
import com.oudmon.ble.base.bluetooth.ListenerKey
import com.oudmon.ble.base.communication.responseImpl.DeviceNotifyListener
import com.oudmon.ble.base.communication.rsp.BaseRspCmd
import com.oudmon.ble.base.communication.rsp.DeviceNotifyRsp
import com.oudmon.ble.base.communication.utils.BLEDataFormatUtils

class BatteryStatusObserver(
    private val context: Context,
    private val callback: (batteryLevel: Int, isCharging: Boolean) -> Unit
) {

    private val listener = object : DeviceNotifyListener() {
        override fun onDataResponse(resultEntity: DeviceNotifyRsp?) {
            if (resultEntity == null || resultEntity.status != BaseRspCmd.RESULT_OK) return

            if (resultEntity.dataType == 0x0c) {
                try {
                    val battery = BLEDataFormatUtils.bytes2Int(byteArrayOf(resultEntity.loadData[1]))
                    val charging = BLEDataFormatUtils.bytes2Int(byteArrayOf(resultEntity.loadData[2]))
                    val isCharging = charging > 0

                    Log.d("BatteryObserver", "Battery: $battery%, Charging: $isCharging")
                    callback(battery, isCharging)
                } catch (e: Exception) {
                    Log.e("BatteryObserver", "Error parsing battery data: ${e.message}")
                }
            }
        }
    }

    fun startObserving() {
        BleOperateManager.getInstance().addOutDeviceListener(ListenerKey.Temperature, listener)
    }

    fun stopObserving() {
        BleOperateManager.getInstance().removeNotifyListener(ListenerKey.All)
    }
}