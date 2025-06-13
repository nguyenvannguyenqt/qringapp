package com.example.ringqrapp.utils

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions

object BluetoothPermissionUtil {
    fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.isEnabled == true
    }

    fun requestBluetoothPermissions(activity: Activity, callback: OnPermissionCallback) {
        XXPermissions.with(activity)
                .permission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                .request(callback)
    }

    fun hasBluetoothPermissions(context: Context): Boolean {
        val permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        return XXPermissions.isGranted(context, permissions)
    }
}
