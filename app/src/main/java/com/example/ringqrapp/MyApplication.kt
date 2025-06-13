package com.example.ringqrapp

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.ringqrapp.bluetooth.BluetoothReceiver
import com.example.ringqrapp.bluetooth.MyBluetoothReceiver
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.oudmon.ble.base.bluetooth.BleAction
import com.oudmon.ble.base.bluetooth.BleOperateManager
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import kotlin.properties.Delegates

@HiltAndroidApp
class MyApplication : Application() {
    var hardwareVersion: String = ""
    var firmwareVersion:String =""

    override fun onCreate() {
        super.onCreate()
        val intentFilter = BleAction.getIntentFilter()
        val myBleReceiver = MyBluetoothReceiver(this)
        //Đăng ký MyBluetoothReceiver để nhận các sự kiện BLE nội bộ (qua LocalBroadcastManager).
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(myBleReceiver, intentFilter)

        //khởi tạo BLE SDK và đăng ký BluetoothReceiver.
        initBle()

        //Khởi tạo Firebase SDK.
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
    fun getDataRingDeviceFb() : DatabaseReference
    {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/ring_devices")
    }
    fun getStatusConnectedFb() : DatabaseReference
    {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/status_connected")
    }
    private fun initBle(){
        //Khởi tạo BleOperateManager để quản lý các hoạt động Bluetooth.
        BleOperateManager.getInstance(this)
        BleOperateManager.getInstance().init()

        //Đăng ký BluetoothReceiver để nhận các sự kiện Bluetooth.
        val deviceFilter = BleAction.getDeviceIntentFilter()
        val deviceReceiver = BluetoothReceiver()
        ContextCompat.registerReceiver(
            this,
            deviceReceiver,
            deviceFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        CONTEXT = applicationContext

    }
    fun getDeviceIntentFilter(): IntentFilter? {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        return intentFilter
    }

    // Lấy thư mục gốc của ứng dụng, nơi lưu trữ dữ liệu và tệp tin.
    fun getAppRootFile(context: Context): File {
        // /storage/emulated/0/Android/data/pack_name/files
        return if(context.getExternalFilesDir("")!=null){
            context.getExternalFilesDir("")!!
        }else{
            val externalSaveDir = context.externalCacheDir
            externalSaveDir ?: context.cacheDir
        }

    }

    companion object{
        private var application:Application? = null
        var CONTEXT :Context by Delegates.notNull()
        fun getApplication(): Application? {
            if (application == null) {
                Log.d("MyApplication", "getApplication: application is null, initializing...")
            }
            return application!!
        }
        val getInstance : MyApplication by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            MyApplication()
        }
        private const val FIREBASE_URL = "https://ringqrapp-default-rtdb.firebaseio.com"
        operator fun get(context: Context?): MyApplication {
            return context!!.applicationContext as MyApplication
        }
    }
}