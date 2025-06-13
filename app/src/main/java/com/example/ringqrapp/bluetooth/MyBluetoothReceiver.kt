package com.example.ringqrapp.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.ringqrapp.MyApplication
import com.oudmon.ble.base.bluetooth.DeviceManager
import com.oudmon.ble.base.bluetooth.QCBluetoothCallbackCloneReceiver
import com.oudmon.ble.base.communication.Constants
import com.oudmon.ble.base.communication.LargeDataHandler
import org.greenrobot.eventbus.EventBus

//xử lý callback từ BluetoothReceiver - nhận các sự kiện trả về từ smart ring thông qua Bluetooth Low Energy (BLE)sau khi đã bắt đầu kết nối hoặc gửi lệnh từ nơi khác trong app.
// Lắng nghe callback nội bộ từ SDK QRing khi kết nối thành công hoặc thất bại, từ đó cập nhật BluetoothEvent(true/false) → liên quan trực tiếp đến việc hoàn tất connect() trong RingRepository
class MyBluetoothReceiver(private var mContext: Context) : QCBluetoothCallbackCloneReceiver() {
    // Override các phương thức để xử lý các sự kiện Bluetooth
    // Ví dụ: onDeviceConnected, onDeviceDisconnected, onDataReceived, v.v.

    // Nhận thông báo khi thiết bị được kết nối
    override fun onServiceDiscovered() {
        LargeDataHandler.getInstance().initEnable()
        EventBus.getDefault().post(BluetoothEvent(true)) // Thông báo Bluetooth đã bật
    }

    // Nhận trạng thái kết nối BLE
    override fun connectStatue(device: BluetoothDevice?, connected: Boolean) {
        if (device != null && connected) {
            // Khi kết nối thành công, có thể lưu địa chỉ MAC của thiết bị
            val deviceName = if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.BLUETOOTH
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                device.name ?: "Unknown Device"
            } else {
                "Unknown Device"
            }
            DeviceManager.getInstance().deviceName = deviceName
            EventBus.getDefault().post(BluetoothEvent(true))
            Log.i("BluetoothReceiver", "Kết nối thành công với thiết bị: $deviceName")

        } else {
            // Khi ngắt kết nối, có thể gửi sự kiện để thông báo cho các thành phần khác trong app
            EventBus.getDefault().post(BluetoothEvent(false)) // Thông báo Bluetooth đã tắt
            Log.i(
                "BluetoothReceiver",
                "Ngắt kết nối với thiết bị : ${device?.name ?: "Unknown Device"}"
            )
        }
    }

    // Nhận dữ liệu từ ring (read) , từ thiết bị và đọc thông tin firmware, hardware
    override fun onCharacteristicRead(uuid: String?, data: ByteArray?) {
        if (uuid != null && data != null) {
            val version = String(data, Charsets.UTF_8)
            when (uuid) {
                Constants.CHAR_FIRMWARE_REVISION.toString() -> {
                    Log.e("rom----", version)
                    // Firmware version
                    MyApplication.getInstance.firmwareVersion = version
                }

                Constants.CHAR_HW_REVISION.toString() -> {
                    Log.e("hardware----", version)
                    // Hardware version
                    MyApplication.getInstance.hardwareVersion = version
                }
            }
        }
    }

    //Nhận dữ liệu từ ring (notify) , từ thiết bị
    override fun onCharacteristicChange(address: String?, uuid: String?, data: ByteArray?) {
        super.onCharacteristicChange(address, uuid, data)
    }
}