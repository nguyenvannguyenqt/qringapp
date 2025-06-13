package com.example.ringqrapp.devices

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ringqrapp.BaseActivity
import com.example.ringqrapp.FeaturesDeviceActivity
import com.example.ringqrapp.SaveDeviceActivity
import com.example.ringqrapp.bluetooth.BluetoothUtils
import com.example.ringqrapp.databinding.ActivityDeviceBindBinding
import com.example.ringqrapp.model.ConnectionState
import com.example.ringqrapp.model.RingDevice
import com.example.ringqrapp.rings.RingManager
import com.oudmon.ble.base.communication.CommandHandle
import com.oudmon.ble.base.communication.Constants
import com.oudmon.ble.base.communication.req.SimpleKeyReq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeviceBindActivity : BaseActivity() {
    private lateinit var binding: ActivityDeviceBindBinding
    private lateinit var adapter: DeviceListAdapter
    private lateinit var ringManager: RingManager
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val deviceList = mutableListOf<RingDevice>()

    private fun showLoading(show: Boolean) {
        binding.progressIndicator.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBindBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ringManager = RingManager.getInstance(this)
        setupViews()
        observeConnectionState()
    }

    private fun observeConnectionState() {
        scope.launch {
            ringManager.connectionState.collectLatest { state ->
                when (state) {
                    is ConnectionState.Connected -> {
                        showLoading(false)
                        CommandHandle.getInstance().executeReqCmd(
                            SimpleKeyReq(Constants.CMD_BIND_SUCCESS),
                            null
                        )
                        Toast.makeText(this@DeviceBindActivity, "Connected to device", Toast.LENGTH_SHORT).show()
                        val connectedDevice = deviceList.firstOrNull()
                        if (connectedDevice != null) {
                            val intent = Intent(this@DeviceBindActivity, FeaturesDeviceActivity::class.java)
                            intent.putExtra("ring_device", connectedDevice)
                            startActivity(intent)
                        }
                        finish()
                    }
                    is ConnectionState.Connecting -> {
                        showLoading(true)
                    }
                    is ConnectionState.Disconnected -> {
                        showLoading(false)
                    }
                    is ConnectionState.Error -> {
                        showLoading(false)
                        Toast.makeText(this@DeviceBindActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    override fun setupViews() {
        super.setupViews()
        adapter = DeviceListAdapter(this, deviceList)
        binding.run {
            deviceRcv.layoutManager = LinearLayoutManager(this@DeviceBindActivity)
            deviceRcv.adapter = adapter
            titleBar.tvTitle.text = "Scan Devices"
            titleBar.ivNavigateBefore.setOnClickListener {
                finish()
            }
        }

        adapter.setOnItemClickListener { _, _, position ->
            val ringDevice = deviceList[position]
            ringManager.connect(ringDevice)
        }

        binding.fabDisconnectedDevice.setOnClickListener{
            Toast.makeText(this,"Disconnected devices",Toast.LENGTH_SHORT).show()
            // Handle disconnected devices
            ringManager.disconnect()
        }

        binding.btnStartScan.setOnClickListener {
            startScanning()
        }
        binding.btnStopScan.setOnClickListener {
            ringManager.stopScan()
            Toast.makeText(this, "Scanning stopped", Toast.LENGTH_SHORT).show()
        }
        binding.btnToDeviceSave.setOnClickListener {
            startActivity(Intent(this, SaveDeviceActivity::class.java))
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startScanning() {
        deviceList.clear()
        adapter.notifyDataSetChanged()
        ringManager.startScan(this) { ringDevice ->
            if (ringDevice != null) {
                if (!deviceList.contains(ringDevice)) {
                    deviceList.add(ringDevice)
                    deviceList.sortByDescending { it.rssi }
                    adapter.notifyDataSetChanged()
                }
            }
        }
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (deviceList.isEmpty()) {
                Toast.makeText(this, "No devices found", Toast.LENGTH_SHORT).show()
            }
        }, 10000)
    }

    // Optionally, auto-scan on resume if Bluetooth is enabled and permissions are granted
    override fun onResume() {
        super.onResume()
        if (BluetoothUtils.isEnabledBluetooth(this@DeviceBindActivity)) {
            if (deviceList.isEmpty()) {
                startScanning()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ringManager.stopScan()
        scope.cancel()
    }

    companion object {
        private const val REQUEST_ENABLE_BLUETOOTH = 300
    }
}