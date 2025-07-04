package com.example.ringqrapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ringqrapp.adapter.DeviceListAdapter
import com.example.ringqrapp.constant.ConstantKey
import com.example.ringqrapp.databinding.ActivityScanDeviceBinding
import com.example.ringqrapp.model.ConnectionState
import com.example.ringqrapp.model.RingDevice
import com.example.ringqrapp.rings.RingManager
import com.example.ringqrapp.utils.BluetoothPermissionUtil
import com.oudmon.ble.base.communication.CommandHandle
import com.oudmon.ble.base.communication.Constants
import com.oudmon.ble.base.communication.req.SimpleKeyReq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanDeviceActivity : AppCompatActivity() {
    private lateinit var scanBinding: ActivityScanDeviceBinding
    private lateinit var adapter: DeviceListAdapter
    private lateinit var ringManager: RingManager
    private var loadingDialog: Dialog? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val deviceList = mutableListOf<RingDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        scanBinding = ActivityScanDeviceBinding.inflate(layoutInflater)
        setContentView(scanBinding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, scanBinding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setup()
        initFeatures()
        observeConnectionState()

    }

    private fun initFeatures() {
        scanBinding.imgBack.setOnClickListener {
            this.finishAfterTransition()
        }

        scanBinding.imgSupport.setOnClickListener {
            showNotificationDialog(Gravity.CENTER)
        }
        scanBinding.txtScanDevice.setOnClickListener {
            if (!BluetoothPermissionUtil.isBluetoothEnabled()) {
                showNotificationBluetoothDialog(Gravity.CENTER)
                return@setOnClickListener
            }
            scanBinding.txtScanDevice.visibility = View.GONE
            scanBinding.constraintLayoutScanDevice.visibility = View.VISIBLE
            scanBinding.lottieLoadingScanDevice.playAnimation()
            scanBinding.lottieLoadingScanDevice.progress = 0.5f
            handleScanDevice()
        }
        adapter.setOnItemClickListener { _, _, position ->
            val ringDevice = deviceList[position]
            showLoading(true)
            ringManager.connect(ringDevice)
        }
    }

    private fun setup() {
        ringManager = RingManager.getInstance(this@ScanDeviceActivity)

        adapter = DeviceListAdapter(this, deviceList)
        scanBinding.rcvScanDevice.layoutManager = LinearLayoutManager(this@ScanDeviceActivity)
        scanBinding.rcvScanDevice.adapter = adapter

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
                        Toast.makeText(
                            this@ScanDeviceActivity,
                            "Đã  kết nối thiết bị",
                            Toast.LENGTH_SHORT
                        ).show()

                        withContext(Dispatchers.Main)
                        {
                            val connectedDevice = deviceList.firstOrNull()
                            if (connectedDevice != null) {
                                val resultIntent = Intent()
                                resultIntent.putExtra(ConstantKey.KEY_DEVICE_SCAN, connectedDevice)
                                setResult(RESULT_OK,resultIntent)
                                finish()
                            }
                            finish()
                        }
                    }

                    is ConnectionState.Error -> {
                        showLoading(false)
                        Toast.makeText(this@ScanDeviceActivity, state.message, Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {
                    }
                }
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleScanDevice() {
        deviceList.clear()
        adapter.notifyDataSetChanged()
        ringManager.startScan(
            this,
            onDeviceFound = { ringDevice ->
                if (!deviceList.contains(ringDevice)) {
                    runOnUiThread { // Đảm bảo cập nhật UI trên luồng chính
                        scanBinding.constraintLayoutRcvDevice.visibility = View.VISIBLE
                        deviceList.add(ringDevice)
                        deviceList.sortByDescending { it.rssi }
                        adapter.notifyDataSetChanged()
                    }
                }
            },
            onScanFinished = {
                runOnUiThread { // Đảm bảo cập nhật UI trên luồng chính
                    scanBinding.lottieLoadingScanDevice.cancelAnimation()
                    scanBinding.constraintLayoutScanDevice.visibility = View.GONE
                    scanBinding.txtScanDevice.visibility = View.VISIBLE
                    scanBinding.txtScanDevice.text =
                        resources.getString(R.string.text_again_scan_device)
                    if (deviceList.isEmpty()) {
                        scanBinding.constraintLayoutRcvDevice.visibility = View.GONE
                    }
                }
            },
        )
    }

    private fun showNotificationBluetoothDialog(gravity: Int) {
        val dialog = Dialog(this@ScanDeviceActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_item_dialog_bluetooth)

        val btnAccept: Button = dialog.findViewById(R.id.btn_accept)
        btnAccept.setOnClickListener {
            dialog.dismiss()
        }
        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(0))
        val windowAttribute = window.attributes
        windowAttribute.gravity = gravity

        window.attributes = windowAttribute
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showNotificationDialog(gravity: Int) {
        val dialog = Dialog(this@ScanDeviceActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_item_dialog_support)

        val btnAccept: Button = dialog.findViewById(R.id.btn_accept)
        btnAccept.setOnClickListener {
            dialog.dismiss()
        }
        val window = dialog.window ?: return
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setBackgroundDrawable(ColorDrawable(0))
        val windowAttribute = window.attributes
        windowAttribute.gravity = gravity

        window.attributes = windowAttribute
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        ringManager.stopScan()
        scope.cancel()
    }

    private fun showLoading(isTrue: Boolean) {

        if (loadingDialog == null) {
            loadingDialog = Dialog(this)
            loadingDialog!!.setContentView(R.layout.dialog_connected_device_loading)
            loadingDialog!!.setCancelable(false)
        }

        if (isTrue) {
            loadingDialog!!.show()
        } else {
            loadingDialog!!.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        loadingDialog?.dismiss()
    }
}