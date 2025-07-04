package com.example.ringqrapp.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ringqrapp.R
import com.example.ringqrapp.activity.feature_options.ThemeActivity
import com.example.ringqrapp.adapter.FeatureOptionAdapter
import com.example.ringqrapp.constant.FunctionGlobal
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.FragmentProfileBinding
import com.example.ringqrapp.utils.LastItemDividerDecoration
import com.example.ringqrapp.interfaces.IFeatureOptionClickListener
import com.example.ringqrapp.model.FeatureOption
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.example.ringqrapp.ScanDeviceActivity
import com.example.ringqrapp.activity.feature_options.FaqQuestionActivity
import com.example.ringqrapp.activity.feature_options.PrincipleActivity
import com.example.ringqrapp.constant.ConstantKey
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment(), IFeatureOptionClickListener {
    private var profileBinding: FragmentProfileBinding? = null
    private lateinit var featureOptionAdapter:FeatureOptionAdapter
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val binding get() = profileBinding!!

    private lateinit var featureOptions:List<FeatureOption>

    private lateinit var ringManager: RingManager

    private var activityResultDevice = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (result.data != null) {
                val deviceResult: RingDevice? =
                    result.data?.getSerializableExtra(ConstantKey.KEY_DEVICE_SCAN) as RingDevice?
                Log.d("ResultDeviceScan", "callback result return deviceResult = ${deviceResult}")

                deviceResult?.let {
                    handleDeviceResult(it)
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (profileBinding == null) {
            profileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        }
        ringManager = RingManager.getInstance(requireContext())
        //observeConnectionState()
        initFeatures()
        return binding.root
    }

    private fun observeConnectionState() {
        scope.launch {
            ringManager.connectionState.collectLatest { state ->
                when (state) {
                    is ConnectionState.Connected -> {
                        withContext(Dispatchers.Main)
                        {
                            Toast.makeText(requireContext(), "device connected", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    is ConnectionState.Error -> {

                    }

                    else -> {
                    }
                }
            }
        }
    }

    private fun handleDeviceResult(ringDevice: RingDevice) {
        profileBinding!!.constraintLayoutConnected.visibility = View.VISIBLE
        profileBinding!!.constraintLayoutNotConnected.visibility = View.GONE

        bindRingDeviceData(ringDevice)
    }

    private fun bindRingDeviceData(ringDevice: RingDevice) {
        profileBinding!!.txtNameDevice.text = ringDevice.name
        profileBinding!!.txtAddressDevice.text = ringDevice.address
    }

    private fun initFeatures() {
        binding.btnConnectedDevice.setOnClickListener {
            if (!BluetoothPermissionUtil.isBluetoothEnabled()) {
                showNotificationDialog(Gravity.CENTER, 1)
                return@setOnClickListener
            }

            val intent = Intent(requireContext(), ScanDeviceActivity::class.java)
            val pairs: Array<Pair<View, String>> = arrayOf(
                Pair(binding.main, "feature_option")
            )
            val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                *pairs
            )
            option.update(
                ActivityOptionsCompat.makeCustomAnimation(
                    requireContext(),
                    R.anim.slide_in_right,
                    R.anim.slide_no_anim
                )
            )
            activityResultDevice.launch(intent)

            /*TransitionHelper.navigateWithTransition(
                requireActivity(),
                ScanDeviceActivity::class.java,
                binding.main,
                "feature_option",
                R.anim.slide_in_right,
                R.anim.slide_no_anim
            )*/
            // khi Bluetooth đã bật
        }
        binding.btnDisConnectedDevice.setOnClickListener {
            showNotificationDialog(Gravity.BOTTOM, type = 2)
        }
        binding.linearLayoutDevice
    }

    private fun showNotificationDialog(gravity: Int, type: Int, position:Int = 0) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        when (type) {
            1 -> dialog.setContentView(R.layout.layout_item_dialog_bluetooth)
            2 -> dialog.setContentView(R.layout.dialog_disconnect_device)
            3, 4 -> dialog.setContentView(R.layout.layout_dialog_measure_unit)
        }

        if (type == 1) {
            val btnAccept: Button = dialog.findViewById(R.id.btn_accept)
            btnAccept.setOnClickListener {
                dialog.dismiss()
            }
        } else if (type == 2) {
            val imgClose: ImageView = dialog.findViewById(R.id.img_close)
            val imgDone: ImageView = dialog.findViewById(R.id.img_done)

            imgClose.setOnClickListener {
                dialog.dismiss()
            }
            imgDone.setOnClickListener {
                binding.constraintLayoutConnected.visibility = View.GONE
                binding.constraintLayoutNotConnected.visibility = View.VISIBLE
                ringManager.disconnect()
                dialog.dismiss()
            }
        } else if (type == 3 || type == 4) {
            val txtCancel: TextView = dialog.findViewById(R.id.txt_cancel)
            val nameMeasureUnit:TextView = dialog.findViewById(R.id.txt_type_value_unit)
            val rd1: TextView = dialog.findViewById(R.id.rb_type_measure_unit_1)
            val rd2: TextView = dialog.findViewById(R.id.rb_type_measure_unit_2)

            nameMeasureUnit.text = resources.getString(if(type == 3) R.string.length_unit else R.string.temperature_unit)
            rd1.text = resources.getString(if(type == 3) R.string.type_length_unit_m else R.string.type_measure_temperature_c)
            rd2.text = resources.getString(if(type == 3) R.string.type_length_unit_anh else R.string.type_measure_temperature_f)
            val imgDone: ImageView = dialog.findViewById(R.id.img_done)


            imgDone.setOnClickListener {
                when (dialog.findViewById<RadioGroup>(R.id.rg_measure_unit).checkedRadioButtonId) {
                    R.id.rb_type_measure_unit_1 ->
                    {
                        featureOptions[position].value = if(type == 3) getString(R.string.value_length_unit_m) else getString(R.string.type_measure_temperature_c)
                        featureOptionAdapter.notifyItemChanged(position)
                    }
                    R.id.rb_type_measure_unit_2 ->
                    {
                        featureOptions[position].value = if(type == 3) getString(R.string.value_length_unit_anh) else getString(R.string.type_measure_temperature_f)
                        featureOptionAdapter.notifyItemChanged(position)
                    }
                }
                dialog.dismiss()
            }
            txtCancel.setOnClickListener {
                dialog.dismiss()
            }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFeatureOptionsRecyclerView()
    }

    private fun setupFeatureOptionsRecyclerView() {
        featureOptions = FunctionGlobal.listFeatureOptions(requireContext())
        featureOptionAdapter = FeatureOptionAdapter(featureOptions, this)
        binding.rcvFeatureOption.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvFeatureOption.setHasFixedSize(false)
        val dividerDrawable = requireContext().resources.getDrawable(R.drawable.divider_white, null)
        val customDivider = LastItemDividerDecoration(dividerDrawable)
        binding.rcvFeatureOption.addItemDecoration(customDivider)
        binding.rcvFeatureOption.adapter = featureOptionAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileBinding = null
    }

    override fun onNextClick(featureOption: FeatureOption,position:Int) {
        when (featureOption.route) {
            "theme_interface" -> {
                TransitionHelper.navigateWithTransition(
                    requireActivity(),
                    ThemeActivity::class.java,
                    binding.main,
                    "feature_option",
                    R.anim.slide_in_right,
                    R.anim.slide_no_anim
                )
            }

            "faq" -> {
                TransitionHelper.navigateWithTransition(
                    requireActivity(),
                    FaqQuestionActivity::class.java,
                    binding.main,
                    "feature_option",
                    R.anim.slide_in_right,
                    R.anim.slide_no_anim
                )
            }

            "background_operation_policy" -> {
                TransitionHelper.navigateWithTransition(
                    requireActivity(),
                    PrincipleActivity::class.java,
                    binding.main,
                    "feature_option",
                    R.anim.slide_in_right,
                    R.anim.slide_no_anim
                )
            }

            "system_metric" -> {
                showNotificationDialog(Gravity.BOTTOM, type = 3, position = position)
            }

            "temperature_unit" -> {
                showNotificationDialog(Gravity.BOTTOM, type = 4, position = position)
            }
        }
    }

    override fun onSwitchToggle(featureOption: FeatureOption, isChecked: Boolean) {
        // Handle switch toggle
    }

}