package com.example.ringqrapp.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.oudmon.ble.base.bluetooth.BleOperateManager
import com.oudmon.ble.base.bluetooth.DeviceManager
import org.greenrobot.eventbus.EventBus

//lắng nghe thay đổi trạng thái Bluetooth hệ thống để kết nối lại hoặc ngắt kết nối thiết bị.và tự động gọi lại connectDirectly() nếu Bluetooth bật lại hoặc mất kết nối
class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        // Không cần check BLUETOOTH_CONNECT cho SDK 30
        when (intent.action) {
            // Khi Bluetooth được bật
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                val connectState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
                if (connectState == BluetoothAdapter.STATE_OFF) { // STATE_ON
                    Log.i("BluetoothReceiver", "Bluetooth đã tắt")
                    BleOperateManager.getInstance()
                        .setBluetoothTurnOff(false) //sử dụng hàm connect/disconnect của SDK từ hãng.
                    BleOperateManager.getInstance().disconnect() // Ngắt kết nối nếu Bluetooth bị tắt
                    // Gửi sự kiện Bluetooth đã tắt
                    EventBus.getDefault()
                        .post(BluetoothEvent(false)) //Tất cả các thành phần đã đăng ký BluetoothEvent sẽ nhận được callback ngay
                } else if (connectState == BluetoothAdapter.STATE_ON) { // STATE_OFF
                    Log.i("BluetoothReceiver", "Bluetooth đã bật")
                    BleOperateManager.getInstance().setBluetoothTurnOff(true)
                    // Kết nối lại với thiết bị đã lưu
                    BleOperateManager.getInstance().reConnectMac =
                        DeviceManager.getInstance().deviceAddress // DivceManager quản lý địa chỉ MAC của thiết bị đã ghép đôi trước đó.
                    // Kết nối trực tiếp với địa chỉ MAC đã lưu
                    BleOperateManager.getInstance()
                        .connectDirectly(DeviceManager.getInstance().deviceAddress)
                }
            }

            BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                // Xử lý khi trạng thái ghép đôi của thiết bị Bluetooth thay đổi
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    val deviceName = if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH
                    ) == PackageManager.PERMISSION_GRANTED) {
                        device.name ?: "Unknown Device"
                    } else {
                        "Unknown Device"
                    }
                    when (device.bondState) {
                        BluetoothDevice.BOND_BONDED -> Log.i(
                            "BluetoothReceiver",
                            "Đã gapi đôi với $deviceName"
                        )

                        BluetoothDevice.BOND_NONE -> Log.i(
                            "BluetoothReceiver",
                            "Đã hủy ghép đôi với $deviceName"
                        )
                    }
                }
            }

            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                // Xử lý khi thiết bị Bluetooth được kết nối
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    val deviceName = if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH
                    ) == PackageManager.PERMISSION_GRANTED) {
                        device.name ?: "Unknown Device"
                    } else {
                        "Unknown Device"
                    }
                    Log.i("BluetoothReceiver", "Đã kết nối với $deviceName")
                }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                // Xử lý khi thiết bị Bluetooth bị ngắt kết nối
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    val deviceName = if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH
                    ) == PackageManager.PERMISSION_GRANTED) {
                        device.name ?: "Unknown Device"
                    } else {
                        "Unknown Device"
                    }
                    Log.i("BluetoothReceiver", "Đã ngắt kết nối với $deviceName")
                    // Gửi sự kiện Bluetooth đã tắt
                    EventBus.getDefault().post(BluetoothEvent(false))
                }
            }

        }
    }
}