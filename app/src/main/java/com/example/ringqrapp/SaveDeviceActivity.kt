package com.example.ringqrapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ringqrapp.adapter.DeviceSaveAdapter
import com.example.ringqrapp.databinding.ActivitySaveDeviceBinding
import com.example.ringqrapp.interfaces.INetworkListener
import com.example.ringqrapp.interfaces.IOnClickDeviceListener
import com.example.ringqrapp.model.InformationRingDevice
import com.example.ringqrapp.utils.NetworkMonitor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class SaveDeviceActivity : AppCompatActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private lateinit var binding: ActivitySaveDeviceBinding
    private lateinit var deviceSaveAdapter: DeviceSaveAdapter
    private var mListDeviceSave: MutableList<InformationRingDevice>? = null
    private var booleanConnected = false

    private var activityResultDevice = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySaveDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        lifecycleScope.launch(Dispatchers.Main) {
            // Bước 1: Gọi và chờ kết quả từ hàm suspend
            val deviceList = getDataDeviceFB()
            // Bước 2: Sau khi có dữ liệu, mới cập nhật UI
            setDataToRecyclerView(deviceList)
        }

        networkMonitor = NetworkMonitor(this)
        networkMonitor.register()
        networkMonitor.setListener(object : INetworkListener {
            override fun onConnected() {
                Toast.makeText(this@SaveDeviceActivity, "Network Connected", Toast.LENGTH_SHORT)
                    .show()
                booleanConnected = true
            }

            override fun onDisconnected() {
                Toast.makeText(this@SaveDeviceActivity, "Network Disconnected", Toast.LENGTH_SHORT)
                    .show()
                booleanConnected = false
            }

        })
    }

    private suspend fun getDataDeviceFB(): MutableList<InformationRingDevice> {
        return withContext(Dispatchers.IO) {
            try {
                val dataSnapshot = MyApplication[this@SaveDeviceActivity]
                    .getDataRingDeviceFb()
                    .get().await()

                if (mListDeviceSave != null)
                    mListDeviceSave!!.clear()
                else
                    mListDeviceSave = mutableListOf()

                Log.d("SaveDeviceActivity", "DataSnapshot: ${mListDeviceSave!!.size}")

                for (snapshot in dataSnapshot.children) {
                    val informationRingDevice = snapshot.getValue(InformationRingDevice::class.java)
                    if (informationRingDevice != null) {
                        println("InformationRingDevice: $informationRingDevice")
                        mListDeviceSave!!.add(informationRingDevice)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mListDeviceSave!!
        }
    }

    private fun setDataToRecyclerView(deviceList: MutableList<InformationRingDevice>) {
        deviceSaveAdapter = DeviceSaveAdapter(deviceList, object : IOnClickDeviceListener {
            override fun onClickDevice(device: InformationRingDevice) {
                // Handle click event for device details
                handleClickDevice(device)
            }

            override fun onDeleteDevice(position: Int, device: InformationRingDevice) {;
                // Handle delete event
                //show dialog confirm default
                val type: Int = if (booleanConnected) 2 else 1
                val dialog = AlertDialog.Builder(this@SaveDeviceActivity)
                    .setTitle("Xác nận xóa")
                    .setMessage("Bạn đã chắc chắn rằng muốn xóa smart ring không?")
                    .setPositiveButton("Xóa") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        deleteDevice(position, device, type)
                    }
                    .setNegativeButton("Hủy") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                dialog.show()
            }
        })
        Log.d("SaveDeviceActivity", "Adapter Size: ${deviceSaveAdapter.itemCount}")
        binding.rcvDeviceSave.apply {
            layoutManager = LinearLayoutManager(this@SaveDeviceActivity)
            setHasFixedSize(false)
            adapter = deviceSaveAdapter
        }
    }

    private fun handleClickDevice(device: InformationRingDevice) {
        Toast.makeText(this, "Clicked on device: ${device.ringDevice?.name}", Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(this, DeviceDetailActivity::class.java)
        intent.putExtra("device", device)
        activityResultDevice.launch(intent)
    }

    private fun deleteDevice(position: Int, device: InformationRingDevice, type: Int) {

        // Show a loading dialog while the device is being removed
        if (type == 1) {
            mListDeviceSave?.removeAt(position)
            deviceSaveAdapter.notifyItemRemoved(position)
            Toast.makeText(
                this@SaveDeviceActivity,
                "Xóa thiết bị thành công",
                Toast.LENGTH_SHORT
            ).show()
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                if (device.ringDevice?.address != null) {
                    MyApplication[this@SaveDeviceActivity]
                        .getDataRingDeviceFb()
                        .child(device.ringDevice.address)
                        .removeValue().await()

                    withContext(Dispatchers.Main) {
                        if(type == 2)
                        {
                            mListDeviceSave?.removeAt(position)
                            deviceSaveAdapter.notifyItemRemoved(position)
                            Toast.makeText(
                                this@SaveDeviceActivity,
                                "Xóa thiết bị thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SaveDeviceActivity,
                        "Failed to remove device",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkMonitor.unregister()
    }
}