package com.example.ringqrapp.rings

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.ringqrapp.bluetooth.BluetoothEvent
import com.example.ringqrapp.bluetooth.BluetoothUtils
import com.example.ringqrapp.interfaces.IRingManager
import com.example.ringqrapp.model.ConnectionState
import com.example.ringqrapp.model.RingDevice
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.oudmon.ble.base.bean.SleepDisplay
import com.oudmon.ble.base.bluetooth.BleOperateManager
import com.oudmon.ble.base.bluetooth.DeviceManager
import com.oudmon.ble.base.bluetooth.ListenerKey
import com.oudmon.ble.base.communication.CommandHandle
import com.oudmon.ble.base.communication.Constants
import com.oudmon.ble.base.communication.req.SimpleKeyReq
import com.oudmon.ble.base.communication.rsp.BaseRspCmd
import com.oudmon.ble.base.communication.rsp.BatteryRsp
import com.oudmon.ble.base.scan.BleScannerHelper
import com.oudmon.ble.base.scan.ScanRecord
import com.oudmon.ble.base.scan.ScanWrapperCallback
import com.oudmon.ble.base.util.ISleepCallback
import com.oudmon.ble.base.util.SleepAnalyzerUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.atomic.AtomicBoolean
import com.example.ringqrapp.utils.BluetoothPermissionUtil

class RingManager private constructor(
    private val context: Context,
) : IRingManager {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val scanTimeoutHandler = Handler(Looper.getMainLooper())
    private val isScanning = AtomicBoolean(false)

    init {
        EventBus.getDefault().register(this)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: RingManager? = null

        fun getInstance(context: Context): RingManager {
            return instance ?: synchronized(this) {
                instance ?: RingManager(context.applicationContext).also { instance = it }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBluetoothEvent(event: BluetoothEvent) {
        _connectionState.value =
            if (event.connect) ConnectionState.Connected else ConnectionState.Disconnected
        if (event.connect && DeviceManager.getInstance().deviceAddress.isNotEmpty()) {
            scope.launch {
                try {
                    BleOperateManager.getInstance()
                        .connectDirectly(DeviceManager.getInstance().deviceAddress)
                } catch (e: Exception) {
                    _connectionState.value = ConnectionState.Error("Reconnect failed: ${e.message}")
                }
            }
        }
    }

    override fun startScan(activity: Activity, callback: (RingDevice?) -> Unit) {
        if (!BluetoothUtils.isEnabledBluetooth(context)) {
            _connectionState.value = ConnectionState.Error("Bluetooth is disabled")
            callback(null)
            return
        }

        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        XXPermissions.with(activity)
            .permission(permissions)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (all) startScanWithPermission(callback)
                    else {
                        _connectionState.value = ConnectionState.Error("Permissions denied")
                        callback(null)
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    _connectionState.value = ConnectionState.Error(
                        if (never) "Permissions permanently denied" else "Permissions denied"
                    )
                    callback(null)
                }
            })
    }

    private fun startScanWithPermission(callback: (RingDevice?) -> Unit) {
        if (isScanning.getAndSet(true)) {
            Log.d("RingManager", "Scan already in progress")
            return
        }

        scanTimeoutHandler.postDelayed({
            stopScan()
            callback(null)
        }, 10000)

        BleScannerHelper.getInstance().scanDevice(
            context,
            null,
            object : ScanWrapperCallback {
                override fun onStart() {
                    Log.d("RingManager", "Scan started")
                }

                override fun onStop() {
                    Log.d("RingManager", "Scan stopped")
                    isScanning.set(false)
                    scanTimeoutHandler.removeCallbacksAndMessages(null)
                }

                override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
                    val name = try {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
                            device?.name
                        } else {
                            "Unknown Device"
                        }
                    } catch (e: SecurityException) {
                        null
                    } ?: return
                    Log.d("RingManager", "Scan device : ${device?.name ?: "Unknown Device"}")
                    Log.d("RingManager", "Device found: $name, RSSI: $rssi , Address: ${device?.address}")
                    if (name.startsWith("R")) {
                        callback(RingDevice(name = name, address = device!!.address, rssi = rssi))
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.e("RingManager", "Scan failed: $errorCode")
                    _connectionState.value = ConnectionState.Error("Scan failed: $errorCode")
                    isScanning.set(false)
                    scanTimeoutHandler.removeCallbacksAndMessages(null)
                    callback(null)
                }

                override fun onParsedData(device: BluetoothDevice?, record: ScanRecord?) {
                    if (device == null || record == null) return

                    Log.d("RingManager", "Parsed data from device: ${device.address} , RSSI: ${record.deviceName}")
                }

                override fun onBatchScanResults(p0: MutableList<android.bluetooth.le.ScanResult>?) {
                    if (p0.isNullOrEmpty()) return

                    for (result in p0) {
                        val device = result.device
                        val rssi = result.rssi
                        val name = if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        } else {
                            device.name ?: "Unknown Device"
                        }
                        Log.d("RingManager", "Batch scan result: $name, RSSI: $rssi, Address: ${device.address}")
                        if (name.startsWith("R")) {
                            callback(RingDevice(name = name, address = device.address, rssi = rssi))
                        }
                    }
                }
            }
        )
    }

    override fun stopScan() {
        if (isScanning.getAndSet(false)) {
            BleScannerHelper.getInstance().stopScan(context)
            scanTimeoutHandler.removeCallbacksAndMessages(null)
        }
    }

    override fun connect(device: RingDevice) {
        Log.d("RingManager", "Connecting to device: ${device.name}, Address: ${device.address}")
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            _connectionState.value = ConnectionState.Error("Bluetooth permissions not granted")
            return
        }
        scope.launch {
            try {
                DeviceManager.getInstance().deviceAddress = device.address
                BleOperateManager.getInstance().connectDirectly(device.address)

                //check connection state
                //BleOperateManager.getInstance().addNotifyListener()
                _connectionState.value = ConnectionState.Connecting
            } catch (e: SecurityException) {
                _connectionState.value = ConnectionState.Error("Bluetooth permission denied: ${e.message}")
            } catch (e: Exception) {
                Log.e("RingManager", "Connection failed: ${e.message}")
                _connectionState.value = ConnectionState.Error("Connection failed: ${e.message}")
            }
        }
    }

    override fun disconnect() {
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            _connectionState.value = ConnectionState.Error("Bluetooth permissions not granted")
            return
        }
        scope.launch {
            try {
                BleOperateManager.getInstance().unBindDevice()
                BleOperateManager.getInstance().removeNotifyListener(ListenerKey.All)
                _connectionState.value = ConnectionState.Disconnected
            } catch (e: SecurityException) {
                _connectionState.value = ConnectionState.Error("Bluetooth permission denied: ${e.message}")
            } catch (e: Exception) {
                Log.e("RingManager", "Disconnect failed: ${e.message}")
                _connectionState.value = ConnectionState.Error("Disconnect failed: ${e.message}")
            }
        }
    }

    override fun updateConnectionState(connected: Boolean) {
        _connectionState.value =
            if (connected) ConnectionState.Connected else ConnectionState.Disconnected
    }

    override fun startHeartRateMeasurement(callback: (Int) -> Unit) {
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            callback(-1)
            return
        }
        scope.launch {
            try {
                BleOperateManager.getInstance().manualModeHeart(
                    { res -> if (res.errCode.toInt() == 0) callback(res.value) },
                    false
                )
            } catch (e: SecurityException) {
                Log.e("RingManager", "Heart rate permission denied: ${e.message}")
                callback(-1)
            } catch (e: Exception) {
                Log.e("RingManager", "Heart rate error: ${e.message}")
                callback(-1)
            }
        }
    }

    override fun startBloodOxygenMeasurement(callback: (Int) -> Unit) {
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            callback(-1)
            return
        }
        scope.launch {
            try {
                BleOperateManager.getInstance().manualModeSpO2(
                    { res -> if (res.errCode.toInt() == 0) callback(res.value) },
                    false
                )
            } catch (e: SecurityException) {
                Log.e("RingManager", "SpO2 permission denied: ${e.message}")
                callback(-1)
            } catch (e: Exception) {
                Log.e("RingManager", "SpO2 error: ${e.message}")
                callback(-1)
            }
        }
    }

    // hàm HrcMeasurement được sử dụng để đo nhịp tim,
    // SpO2Measurement để đo nồng độ oxy trong máu,
    // HrvMeasurement để đo biến thiên nhịp tim,
    // TemperatureMeasurement để đo nhiệt độ cơ thể, và SleepSync để đồng bộ hóa dữ liệu giấc ngủ.
    override fun startHrvMeasurement(callback: (Int) -> Unit) {
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            callback(-1)
            return
        }
        scope.launch {
            try {
                BleOperateManager.getInstance().manualModeHrv(
                    { res -> if (res.errCode.toInt() == 0) callback(res.hrv ?: 0) },
                    false
                )

            } catch (e: SecurityException) {
                Log.e("RingManager", "HRV permission denied: ${e.message}")
                callback(-1)
            } catch (e: Exception) {
                Log.e("RingManager", "HRV error: ${e.message}")
                callback(-1)
            }
        }
    }

    override fun startTemperatureMeasurement(callback: (Float) -> Unit) {
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            callback(-1f)
            return
        }
        scope.launch {
            try {
                BleOperateManager.getInstance().manualTemperature(
                    { res -> if (res.errCode.toInt() == 0) callback(res.value.toFloat()) },
                    false
                )
            } catch (e: SecurityException) {
                Log.e("RingManager", "Temperature permission denied: ${e.message}")
                callback(-1f)
            } catch (e: Exception) {
                Log.e("RingManager", "Temperature error: ${e.message}")
                callback(-1f)
            }
        }
    }

    override fun startSleepSync(callback: (String) -> Unit) {
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            callback("")
            return
        }
        scope.launch {
            try {
                val deviceAddress = DeviceManager.getInstance().deviceAddress
                val dayOffset = 0 // 0 = hôm nay, 1 = hôm qua, 2 = hôm kia (tối đa 7 ngày)
                SleepAnalyzerUtils.getInstance().syncSleepReturnSleepDisplay(
                    deviceAddress,
                    dayOffset,
                    object : ISleepCallback {
                        override fun sleepDisplay(p0: SleepDisplay?) {
                            Log.d("RingManager", "Sleep synced: $p0")
                            callback(p0.toString())
                        }
                    }
                )
            } catch (e: SecurityException) {
                Log.e("RingManager", "Sleep sync permission denied: ${e.message}")
                callback("")
            } catch (e: Exception) {
                Log.e("RingManager", "Sleep sync error: ${e.message}")
                callback("")
            }
        }
    }

    fun getBatteryLevel(callback: (Int) -> Unit) {
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            callback(-1)
            return
        }
        scope.launch {
            try {
                CommandHandle.getInstance().executeReqCmd(
                    SimpleKeyReq(Constants.CMD_GET_DEVICE_ELECTRICITY_VALUE)
                ) { result ->
                    if (result.status == BaseRspCmd.RESULT_OK) {
                        callback((result as BatteryRsp).batteryValue)
                        Log.d("Battery", "Battery level: ${result.batteryValue}")
                    }
                }
            } catch (e: SecurityException) {
                Log.e("RingManager", "Battery permission denied: ${e.message}")
                callback(-1)
            } catch (e: Exception) {
                Log.e("RingManager", "Battery error: ${e.message}")
                callback(-1)
            }
        }
    }

    fun cleanup() {
        EventBus.getDefault().unregister(this)
        stopScan()
        disconnect()
        scope.cancel()
        instance = null
    }
}