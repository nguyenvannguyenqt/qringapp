package com.example.ringqrapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.ringqrapp.databinding.ActivityMainBinding
import com.example.ringqrapp.devices.DeviceBindActivity
import com.example.ringqrapp.interfaces.INetworkListener
import com.example.ringqrapp.rings.RingManager
import com.example.ringqrapp.utils.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private lateinit var ringManager: RingManager
    private lateinit var activityBinding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(activityBinding.root)

        activityBinding.heartRateText.text = "Disconnected"
        // Khởi tạo RingManager nếu cần cho các chức năng khác
        ringManager = RingManager.getInstance(this@MainActivity)

        // Khi bấm nút kết nối, mở DeviceBindActivity để quét và chọn thiết bị
        activityBinding.connectButton.setOnClickListener {
            val intent = Intent(this, DeviceBindActivity::class.java)
            startActivity(intent)
        }

        // Đo nhịp tim (nếu đã kết nối thiết bị)
        activityBinding.measureButton.setOnClickListener {
            // ringManager.startHeartRateMonitoring()
            activityBinding.heartRateText.text = "Heart Rate: Measuring..."
        }
        activityBinding.btnDisconnected.setOnClickListener {
            // Ngắt kết nối thiết bị
            ringManager.disconnect()
            activityBinding.heartRateText.text = "Heart Rate: Disconnected"
        }
        networkMonitor.register()
        networkMonitor.setListener(object : INetworkListener {
            override fun onConnected() {
                Toast.makeText(this@MainActivity, "Network Connected", Toast.LENGTH_SHORT).show()
            }

            override fun onDisconnected() {
                Toast.makeText(this@MainActivity, "Network Disconnected", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        ringManager.disconnect()
        networkMonitor.unregister()
    }
}
