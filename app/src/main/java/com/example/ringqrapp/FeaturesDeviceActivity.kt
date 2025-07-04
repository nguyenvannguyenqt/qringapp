package com.example.ringqrapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.ringqrapp.databinding.ActivityFeaturesDeviceBinding
import com.example.ringqrapp.model.InformationRingDevice
import com.example.ringqrapp.model.RingDevice
import com.example.ringqrapp.rings.RingManager
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, featureBinding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

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



        featureBinding.btnGetTemperature.setOnClickListener{
            Toast.makeText(this, "Starting temperature measurement...", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val temperature = ringManager.startTemperatureMeasurement()
                    if (temperature != -1f) {
                        val temperature2 : Float = (temperature/10.0f)
                        Log.d("FEATURES", "nhiet do cua Son Sai Gon : $temperature2 °C")
                        featureBinding.txtTemperatureValue.text = "Temperature: $temperature2 °C"
                    } else {
                        featureBinding.txtTemperatureValue.text = "Temperature: N/A"
                        Toast.makeText(this@FeaturesDeviceActivity, "Failed to get temperature.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("FeaturesDeviceActivity", "Error getting temperature: ${e.message}")
                    Toast.makeText(this@FeaturesDeviceActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        featureBinding.btnGetBloodOxygen.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val oxyTrongMau = ringManager.startBloodOxygenMeasurement()
                    if (oxyTrongMau != null) {
                        Log.d("FEATURES","Oxy trong mau ${oxyTrongMau.value}")
                        Log.d("FEATURES","Oxy trong mau2 ${oxyTrongMau.bloodOxygen}")
                        Log.d("FEATURES","stress ${oxyTrongMau.stress}")
                        featureBinding.txtBloodOxygenValue.text = oxyTrongMau.value.toString()
                    } else {
                        featureBinding.txtBloodOxygenValue.text = "Blood Oxygen: N/A"
                        Toast.makeText(this@FeaturesDeviceActivity, "Failed to get blood oxygen.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("FeaturesDeviceActivity", "Error getting blood oxygen: ${e.message}")
                    Toast.makeText(this@FeaturesDeviceActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        featureBinding.btnGetHeartRate.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                /*try {
                    val value = ringManager.startHeartRateMeasurement()
                    if (value != null) {
                        Log.d("FEATURES","Nhip tim ${value.value}")
                        Log.d("FEATURES","Heart ${value.heart}")
                        Log.d("FEATURES","Heart rate ${value.heartRate}")
                        featureBinding.txtHeartRateValue.text = value.toString()
                    } else {
                        featureBinding.txtHeartRateValue.text = "Heart Rate: N/A"
                        Toast.makeText(this@FeaturesDeviceActivity, "Failed to get heart rate.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("FeaturesDeviceActivity", "Error getting heart rate: ${e.message}")
                    Toast.makeText(this@FeaturesDeviceActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }*/
                try {
                    ringManager.startHeartRateMeasurementCallBack { value ->
                        if (value != null) {
                            Log.d("FEATURES", "Nhip tim ${value.value}")
                            Log.d("FEATURES", "Heart ${value.heart}")
                            Log.d("FEATURES", "Heart rate ${value.heartRate}")
                            featureBinding.txtHeartRateValue.text = value.toString()
                        } else {
                            featureBinding.txtHeartRateValue.text = "Heart Rate: N/A"
                            Toast.makeText(this@FeaturesDeviceActivity, "Failed to get heart rate.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("FeaturesDeviceActivity", "Error getting heart rate: ${e.message}")
                    Toast.makeText(this@FeaturesDeviceActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        featureBinding.btnGetBloodPressure.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val pairBloodPressure = ringManager.startBloodPressureMeasurement()
                    if (pairBloodPressure != null) {
                        featureBinding.txtBloodPressureValue.text = "${pairBloodPressure.first}/${pairBloodPressure.second} mmHg"
                    } else {
                        featureBinding.txtBloodPressureValue.text = "Lỗi đo huyết áp."
                        Toast.makeText(this@FeaturesDeviceActivity, "Failed to get blood pressure.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("FeaturesDeviceActivity", "Error getting blood pressure: ${e.message}")
                    Toast.makeText(this@FeaturesDeviceActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /*featureBinding.btnGetSleep.setOnClickListener {
            Toast.makeText(this, "Starting sleep sync...", Toast.LENGTH_SHORT).show()
            lifecycleScope.launch(Dispatchers.Main) {
                try {
                    val sleepData = ringManager.startSleepSync()
                    if (sleepData.isNotEmpty()) {
                        featureBinding.txtSleepDataValue.text = "Sleep Data: $sleepData"
                    } else {
                        featureBinding.txtSleepDataValue.text = "Sleep Data: N/A"
                        Toast.makeText(this@FeaturesDeviceActivity, "Failed to sync sleep data.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("FeaturesDeviceActivity", "Error syncing sleep data: ${e.message}")
                    Toast.makeText(this@FeaturesDeviceActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }*/

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