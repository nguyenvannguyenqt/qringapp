package com.example.ringqrapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ringqrapp.databinding.ActivityDeviceDetailBinding
import com.example.ringqrapp.model.InformationRingDevice
import com.example.ringqrapp.rings.RingManager

class DeviceDetailActivity : AppCompatActivity() {
    private lateinit var ringManager: RingManager
    private var battery:Int = 0
    private lateinit var binding: ActivityDeviceDetailBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDeviceDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getDataIntent()

        binding.btnUpdateDevice.setOnClickListener {
            handleUpdateDevice()
        }
        binding.btnGetBattery.setOnClickListener {
            ringManager = RingManager.getInstance(this)
            ringManager.getBatteryLevel { batteryLevel ->
                battery = batteryLevel
                binding.txtBatteryValue.text = "Battery Level: $batteryLevel%"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getDataIntent() {
        val device = intent.getSerializableExtra("device") as InformationRingDevice?
        if (device != null) {
            binding.txtDeviceName.text = device.ringDevice!!.name
            binding.txtDeviceAddress.text = device.ringDevice.address
            binding.txtDeviceRssi.text = "RSSI: ${device.ringDevice.rssi}dBm"
        }
    }
    private fun handleUpdateDevice() {

    }
}