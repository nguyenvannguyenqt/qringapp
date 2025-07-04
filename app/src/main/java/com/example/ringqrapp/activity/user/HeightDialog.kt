package com.example.ringqrapp.activity.user

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.NumberPicker.OnValueChangeListener
import com.example.ringqrapp.R
import com.example.ringqrapp.databinding.DialogEditHeightBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HeightDialog : BottomSheetDialogFragment() {

    private var _binding: DialogEditHeightBinding? = null
    private val binding get() = _binding!!


    // Mảng đơn vị chiều cao
    private val heightUnits = arrayOf("cm", "Ft-in")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogEditHeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNumberPickers()
        setupClickListeners()
        // Thiết lập padding để tăng khoảng cách
    }

    private fun setupNumberPickers() {

        // Thiết lập NumberPicker cho giá trị chiều cao
        binding.heightValuePicker.minValue = 50 // Chiều cao tối thiểu 50cm
        binding.heightValuePicker.maxValue = 250 // Chiều cao tối đa 250cm
        binding.heightValuePicker.value = 155 // Giá trị mặc định 155cm
        binding.heightValuePicker.wrapSelectorWheel = false


        // Thiết lập NumberPicker cho đơn vị
        binding.heightUnitPicker.minValue = 0
        binding.heightUnitPicker.maxValue = heightUnits.size - 1
        binding.heightUnitPicker.displayedValues = heightUnits
        binding.heightUnitPicker.value = 0 // Mặc định chọn "cm" (index 0)
        binding.heightUnitPicker.wrapSelectorWheel = false


        // Tùy chỉnh giao diện NumberPicker


        // Lắng nghe thay đổi đơn vị để điều chỉnh phạm vi giá trị
        binding.heightUnitPicker.setOnValueChangedListener(OnValueChangeListener { picker, oldVal, newVal ->
            if (newVal == 0) { // cm
                binding.heightValuePicker.minValue = 50
                binding.heightValuePicker.maxValue = 250
                if (binding.heightValuePicker.value < 50) {
                    binding.heightValuePicker.value = 155
                }
            } else { // Ft-in
                binding.heightValuePicker.minValue = 36 // 3 feet
                binding.heightValuePicker.maxValue = 96 // 8 feet
                if (binding.heightValuePicker.value > 96) {
                    binding.heightValuePicker.value = 66 // ~5'6"
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        binding.btnSave.setOnClickListener {
            val selectedHeight = binding.heightValuePicker.value
            val selectedUnit = heightUnits[binding.heightUnitPicker.value]
            
            val result = Bundle().apply {
                putInt("selected_height", selectedHeight)
                putString("selected_unit", selectedUnit)
            }
            parentFragmentManager.setFragmentResult("height_key", result)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 