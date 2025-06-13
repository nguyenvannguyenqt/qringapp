package com.example.ringqrapp.bluetooth

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager

//hỗ trợ kiểm tra trạng thái Bluetooth : Kiểm tra thiết bị có hỗ trợ BLE không.,Kiểm tra Bluetooth có bật không.,Kiểm tra phiên bản Android (Lollipop trở lên).
object BluetoothUtils {
    fun isEnabledBluetooth(context : Context) : Boolean{
        return try{
            val bluetoothAdapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) &&
                    bluetoothAdapter != null && bluetoothAdapter.isEnabled
        }catch (e: Exception)
        {
            e.printStackTrace()
            false;
        }
    }
    fun hasLollipop(): Boolean = true
}