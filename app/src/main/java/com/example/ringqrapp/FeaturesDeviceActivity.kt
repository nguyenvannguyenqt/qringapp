package com.example.ringqrapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.ringqrapp.databinding.ActivityFeaturesDeviceBinding
import com.example.ringqrapp.model.InformationRingDevice
import com.example.ringqrapp.model.RingDevice
import com.example.ringqrapp.rings.RingManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FeaturesDeviceActivity : AppCompatActivity() {
    private var batteryInit: Int = 0
    private lateinit var featureBinding: ActivityFeaturesDeviceBinding
    private lateinit var ringManager: RingManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        featureBinding = ActivityFeaturesDeviceBinding.inflate(layoutInflater)
        setContentView(featureBinding.root)

        ringManager = RingManager.getInstance(this)

        // Nhận đối tượng RingDevice từ Intent (Serializable)
        val ringDevice = intent.getSerializableExtra("ring_device") as? RingDevice

        if (ringDevice != null) {
            featureBinding.txtDeviceName.text = "Name: ${ringDevice.name}"
            featureBinding.txtDeviceAddress.text = "Address: ${ringDevice.address}"
            featureBinding.txtDeviceRssi.text = "RSSI: ${ringDevice.rssi} dBm"
        }


        featureBinding.btnGetBattery.setOnClickListener {
            ringManager.getBatteryLevel { batteryLevel ->
                if (batteryLevel != -1) {
                    featureBinding.txtBatteryValue.text = "Battery Level: $batteryLevel%"
                    batteryInit = batteryLevel
                } else {
                    featureBinding.txtBatteryValue.text = "Battery Level: N/A"
                    Toast.makeText(this, "Failed to get battery level.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        featureBinding.btnDisconnect.setOnClickListener {
            ringManager.disconnect()
            Toast.makeText(this, "Disconnected from device", Toast.LENGTH_SHORT).show()
            finish() // Đóng Activity sau khi ngắt kết nối
        }

        featureBinding.btnSaveDevice.setOnClickListener {
            Toast.makeText(this, "Saving device information...", Toast.LENGTH_SHORT).show()
            val informationRingDevice = InformationRingDevice(
                battery = batteryInit.toString().substringAfter(": ").toIntOrNull() ?: 0,
                valueUpdate = "Updated at: ${System.currentTimeMillis()}",
                ringDevice = ringDevice ?: RingDevice("Unknown", "Unknown", 0)
            )
            // Lấy deviceRef
            val deviceRef = MyApplication[this@FeaturesDeviceActivity]
                .getDataRingDeviceFb()
                .child(ringDevice?.address.toString())

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    // Bước 1: Dùng get().await() để đọc dữ liệu MỘT LẦN DUY NHẤT
                    // Đây là một suspend function, nó sẽ tạm dừng coroutine cho đến khi có kết quả
                    val snapshot = deviceRef.get().await()

                    // Bước 2: Kiểm tra kết quả một cách tuần tự
                    if (snapshot.exists()) {
                        // Nếu đã tồn tại, chỉ cần thông báo và kết thúc
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@FeaturesDeviceActivity,
                                "Thiết bị đã tồn tại trong database.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    } else {
                        deviceRef.setValue(informationRingDevice).await()
                        // Thông báo thành công
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@FeaturesDeviceActivity,
                                "Lưu thông tin thiết bị thành công.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                } catch (e: Exception) {
                    // Bắt tất cả các lỗi có thể xảy ra (mất mạng, lỗi quyền...)
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@FeaturesDeviceActivity,
                            "Đã xảy ra lỗi: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

    }
}