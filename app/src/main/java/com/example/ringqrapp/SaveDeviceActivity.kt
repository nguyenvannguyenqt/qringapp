package com.example.ringqrapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ringqrapp.adapter.DeviceSaveAdapter
import com.example.ringqrapp.databinding.ActivitySaveDeviceBinding
import com.example.ringqrapp.model.InformationRingDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SaveDeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveDeviceBinding
    private lateinit var deviceSaveAdapter: DeviceSaveAdapter
    private var mListDeviceSave:MutableList<InformationRingDevice>? = null
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
    }
    private suspend fun getDataDeviceFB(): MutableList<InformationRingDevice> {
        return withContext(Dispatchers.IO) {
            try {
                val dataSnapshot = MyApplication[this@SaveDeviceActivity]
                    .getDataRingDeviceFb()
                    .get().await()

                if(mListDeviceSave != null)
                    mListDeviceSave!!.clear()
                else
                    mListDeviceSave = mutableListOf()

                Log.d("SaveDeviceActivity", "DataSnapshot: ${mListDeviceSave!!.size}")

                for(snapshot in dataSnapshot.children) {
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
        deviceSaveAdapter = DeviceSaveAdapter(deviceList)
        Log.d("SaveDeviceActivity", "Adapter Size: ${deviceSaveAdapter.itemCount}")
        binding.rcvDeviceSave.apply {
            layoutManager = LinearLayoutManager(this@SaveDeviceActivity)
            setHasFixedSize(false)
            adapter = deviceSaveAdapter
        }
    }
}