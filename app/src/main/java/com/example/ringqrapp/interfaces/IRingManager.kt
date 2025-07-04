    package com.example.ringqrapp.interfaces

    import android.app.Activity
    import com.example.ringqrapp.model.ConnectionState
    import com.example.ringqrapp.model.RingDevice
    import com.oudmon.ble.base.communication.rsp.StartHeartRateRsp
    import kotlinx.coroutines.flow.StateFlow

    interface IRingManager {
        val connectionState: StateFlow<ConnectionState>
        //fun startScan(activity: Activity,callback: (RingDevice?) -> Unit)
        fun startScan(activity: Activity,onDeviceFound: (RingDevice) -> Unit,
                      onScanFinished: () -> Unit)

        fun stopScan()
        fun connect(device: RingDevice)
        fun disconnect()
        fun updateConnectionState(connected: Boolean)
        suspend fun startHeartRateMeasurement(): StartHeartRateRsp?
        fun startHeartRateMeasurementCallBack(callback: (StartHeartRateRsp?) -> Unit)
        suspend fun startBloodOxygenMeasurement(): StartHeartRateRsp?
        suspend fun startBloodPressureMeasurement(): Pair<Int, Int>?
        suspend fun startHrvMeasurement(): Int
        suspend fun startTemperatureMeasurement(): Float
        suspend fun startSleepSync(): String
    }