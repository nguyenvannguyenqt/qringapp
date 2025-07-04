package com.example.ringqrapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ringqrapp.databinding.ActivityMainBinding
import com.example.ringqrapp.interfaces.INetworkListener
import com.example.ringqrapp.model.FeatureOption
import com.example.ringqrapp.model.OptionType
import com.example.ringqrapp.rings.RingManager
import com.example.ringqrapp.utils.NetworkMonitor
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private lateinit var ringManager: RingManager
    private lateinit var activityBinding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(activityBinding.root)

        activityBinding.heartRateText.text = "Disconnected"
        // Khởi tạo RingManager nếu cần cho các chức năng khác
        ringManager = RingManager.getInstance(this@MainActivity)
        firebaseAuth = FirebaseAuth.getInstance()

        // Khi bấm nút kết nối, mở DeviceBindActivity để quét và chọn thiết bị
        /*activityBinding.connectButton.setOnClickListener {
            val intent = Intent(this, DeviceBindActivity::class.java)
            startActivity(intent)
        }*/

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
        networkMonitor = NetworkMonitor(this)
        networkMonitor.register()
        networkMonitor.setListener(object : INetworkListener {
            override fun onConnected() {
                Toast.makeText(this@MainActivity, "Network Connected", Toast.LENGTH_SHORT).show()
            }

            override fun onDisconnected() {
                Toast.makeText(this@MainActivity, "Network Disconnected", Toast.LENGTH_SHORT).show()
            }

        })
        activityBinding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            this.finish()
        }
        val featureOption = FeatureOption(
            iconRes = R.drawable.img,
            title = "Settings",
            idBackgroundIcon = ContextCompat.getDrawable(this, R.drawable.bg_background_circle_blue_light)!!,
            type = OptionType.NAVIGATION,
            route = "settings"
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        ringManager.disconnect()
        networkMonitor.unregister()
    }
}
