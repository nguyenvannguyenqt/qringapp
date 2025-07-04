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
import kotlinx.coroutines.suspendCancellableCoroutine
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.atomic.AtomicBoolean
import com.example.ringqrapp.utils.BluetoothPermissionUtil
import com.oudmon.ble.base.communication.rsp.StartHeartRateRsp
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RingManager private constructor(
    private val context: Context,
) : IRingManager {

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState


    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
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


    override fun startScan(activity: Activity,
                           onDeviceFound: (RingDevice) -> Unit,
                           onScanFinished: () -> Unit) {
        if (!BluetoothUtils.isEnabledBluetooth(context)) {
            _connectionState.value = ConnectionState.Error("Bluetooth is disabled")
            onScanFinished()
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
                    if (all) startScanWithPermission(onDeviceFound, onScanFinished)
                    else {
                        _connectionState.value = ConnectionState.Error("Permissions denied")
                        onScanFinished()
                    }
                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    _connectionState.value = ConnectionState.Error(
                        if (never) "Permissions permanently denied" else "Permissions denied"
                    )
                    onScanFinished()
                }
            })
    }

    private fun startScanWithPermission(onDeviceFound: (RingDevice) -> Unit, onScanFinished: () -> Unit) {
        if (isScanning.getAndSet(true)) {
            Log.d("RingManager", "Scan already in progress")
            return
        }

        scanTimeoutHandler.postDelayed({
            stopScan()
            onScanFinished()
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
                    onScanFinished()
                }

                override fun onLeScan(device: BluetoothDevice?, rssi: Int, scanRecord: ByteArray?) {
                    val name = try {
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.BLUETOOTH
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            device?.name
                        } else {
                            "Unknown Device"
                        }
                    } catch (e: SecurityException) {
                        null
                    } ?: return
                    Log.d("RingManager", "Scan device : ${device?.name ?: "Unknown Device"}")
                    Log.d(
                        "RingManager",
                        "Device found: $name, RSSI: $rssi , Address: ${device?.address}"
                    )
                    if (name.startsWith("R")) {
                        onDeviceFound(RingDevice(name = name, address = device!!.address, rssi = rssi))
                    }
                }

                override fun onScanFailed(errorCode: Int) {
                    Log.e("RingManager", "Scan failed: $errorCode")
                    _connectionState.value = ConnectionState.Error("Scan failed: $errorCode")
                    isScanning.set(false)
                    scanTimeoutHandler.removeCallbacksAndMessages(null)
                    onScanFinished()
                }

                override fun onParsedData(device: BluetoothDevice?, record: ScanRecord?) {
                    if (device == null || record == null) return
                    Log.d(
                        "RingManager",
                        "Parsed data from device: ${device.address} , RSSI: ${record.deviceName}"
                    )
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
                        Log.d(
                            "RingManager",
                            "Batch scan result: $name, RSSI: $rssi, Address: ${device.address}"
                        )
                        if (name.startsWith("R")) {
                            onDeviceFound(RingDevice(name = name, address = device.address, rssi = rssi))
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
                _connectionState.value =
                    ConnectionState.Error("Bluetooth permission denied: ${e.message}")
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
                _connectionState.value =
                    ConnectionState.Error("Bluetooth permission denied: ${e.message}")
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

    override suspend fun startHeartRateMeasurement(): StartHeartRateRsp? = suspendCancellableCoroutine { continuation ->
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        try {
            BleOperateManager.getInstance().manualModeHeart(
                { res ->
                    if (res.errCode.toInt() == 0) {
                        continuation.resume(res)
                    } else {
                        continuation.resume(null)
                    }
                },
                false
            )
        } catch (e: SecurityException) {
            Log.e("RingManager", "Heart rate permission denied: ${e.message}")
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            Log.e("RingManager", "Heart rate error: ${e.message}")
            continuation.resumeWithException(e)
        }
    }

    override fun startHeartRateMeasurementCallBack(callback: (StartHeartRateRsp?) -> Unit) {
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            callback(null)
            return
        }
        try {
            BleOperateManager.getInstance().manualModeHeart(
                { res ->
                    if (res.errCode.toInt() == 0) {
                        callback(res)
                    } else {
                        callback(null)
                    }
                },
                false
            )
        } catch (e: SecurityException) {
            Log.e("RingManager", "Heart rate permission denied: ${e.message}")
            callback(null)
        } catch (e: Exception) {
            Log.e("RingManager", "Heart rate error: ${e.message}")
            callback(null)
        }
    }

    override suspend fun startBloodOxygenMeasurement(): StartHeartRateRsp? = suspendCancellableCoroutine { continuation ->
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        try {
            BleOperateManager.getInstance().manualModeSpO2(
                { res ->
                    if (res.errCode.toInt() == 0) {
                        continuation.resume(res)
                    } else {
                        continuation.resume(null)
                    }
                },
                false
            )
        } catch (e: SecurityException) {
            Log.e("RingManager", "SpO2 permission denied: ${e.message}")
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            Log.e("RingManager", "SpO2 error: ${e.message}")
            continuation.resumeWithException(e)
        }
    }

    override suspend fun startBloodPressureMeasurement(): Pair<Int, Int>? = suspendCancellableCoroutine { continuation ->
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            Log.e("RingManager", "Blood Pressure - No Bluetooth permissions.")
            continuation.resume(null)
            return@suspendCancellableCoroutine
        }
        try {
            BleOperateManager.getInstance().manualModeBP(
                { res ->
                    if (res != null && res.errCode.toInt() == 0) {
                        val systolic = res.sbp
                        val diastolic = res.dbp
                        val bloodPressure = Pair(systolic, diastolic)
                        Log.d("RingManager", "Blood Pressure measured: SBP=$systolic, DBP=$diastolic")
                        continuation.resume(bloodPressure)
                    } else {
                        Log.e("RingManager", "Blood Pressure measurement failed. Error code: ${res?.errCode}")
                        continuation.resume(null)
                    }
                },
                false
            )
        } catch (e: SecurityException) {
            Log.e("RingManager", "Blood Pressure permission denied: ${e.message}")
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            Log.e("RingManager", "Blood Pressure measurement error: ${e.message}")
            continuation.resumeWithException(e)
        }
    }

    override suspend fun startHrvMeasurement(): Int = suspendCancellableCoroutine { continuation ->
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            continuation.resume(-1)
            return@suspendCancellableCoroutine
        }
        try {
            BleOperateManager.getInstance().manualModeHrv(
                { res ->
                    if (res.errCode.toInt() == 0) continuation.resume(res.hrv ?: 0)
                    else continuation.resume(-1)
                },
                false
            )
        } catch (e: SecurityException) {
            Log.e("RingManager", "HRV permission denied: ${e.message}")
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            Log.e("RingManager", "HRV error: ${e.message}")
            continuation.resumeWithException(e)
        }
    }

    override suspend fun startTemperatureMeasurement(): Float = suspendCancellableCoroutine { continuation ->
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            continuation.resume(-1f)
            return@suspendCancellableCoroutine
        }
        try {
            BleOperateManager.getInstance().manualTemperature(
                { res ->
                    if (res.errCode.toInt() == 0) continuation.resume(res.value.toFloat())
                    else continuation.resume(-1f)
                },
                false
            )
        } catch (e: SecurityException) {
            Log.e("RingManager", "Temperature permission denied: ${e.message}")
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            Log.e("RingManager", "Temperature error: ${e.message}")
            continuation.resumeWithException(e)
        }
    }

    override suspend fun startSleepSync(): String = suspendCancellableCoroutine { continuation ->
        if (!BluetoothPermissionUtil.hasBluetoothPermissions(context)) {
            continuation.resume("")
            return@suspendCancellableCoroutine
        }
        try {
            val deviceAddress = DeviceManager.getInstance().deviceAddress
            val dayOffset = 0 // 0 = hôm nay, 1 = hôm qua, 2 = hôm kia (tối đa 7 ngày)
            SleepAnalyzerUtils.getInstance().syncSleepReturnSleepDisplay(
                deviceAddress,
                dayOffset,
                object : ISleepCallback {
                    override fun sleepDisplay(p0: SleepDisplay?) {
                        Log.d("RingManager", "Sleep synced: $p0")
                        p0?.let {
                            Log.d("RingManager", "Sleep data: ${it.sleepTime}")
                            val totalSleepMinutes: Int = it.totalSleepDuration // tổng thời gian ngủ thực tế(Ngủ sâu + Ngủ nông + ngủ REM)
                            val deepSleepMinutes: Int = it.deepSleepDuration // thời gian ngủ sâu
                            val lightSleepMinutes: Int = it.shallowSleepDuration // thời gian ngủ nông
                            val remSleepMinutes: Int = it.rapidDuration // thời gian ngủ REM
                            val awakeMinutes: Int = it.awakeDuration // tổng thời gian thức giấc giữa đêm
                            val startTimeSleep = it.sleepTime // thời gian bắt đầu ngủ
                            val wakeUpTimeRaw: Int = it.wakeTime // thời gian thức giấc
                            val awakeCount: Int = it.wakingCount // số lần thức giấc
                            val sleepDetailsList: List<SleepDisplay.SleepDataBean> = it.list // danh sách các lần ngủ
                            val recordId: Int = it.id // i d của lần ngủ
                            val deviceAddressTmp: String = it.address // địa chỉ của thiết bị
                            val totalDaysWithData: Int = it.totalDays // tổng số ngày có dữ liệu ngủ
                            continuation.resume(it.toString())
                        } ?: run {
                            Log.e("RingManager", "Sleep data is null")
                            continuation.resume("")
                        }
                    }
                }
            )
        } catch (e: SecurityException) {
            Log.e("RingManager", "Sleep sync permission denied: ${e.message}")
            continuation.resumeWithException(e)
        } catch (e: Exception) {
            Log.e("RingManager", "Sleep sync error: ${e.message}")
            continuation.resumeWithException(e)
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