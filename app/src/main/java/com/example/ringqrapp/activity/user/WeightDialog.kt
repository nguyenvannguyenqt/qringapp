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
import com.example.ringqrapp.databinding.DialogEditWeightBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class WeightDialog : BottomSheetDialogFragment() {

    private var _binding: DialogEditWeightBinding? = null
    private val binding get() = _binding!!


    // Mảng đơn vị
    private val heightUnits = arrayOf("kg", "lbs")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditWeightBinding.inflate(inflater, container, false)
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
        binding.weightValuePicker.minValue = 30 //
        binding.weightValuePicker.maxValue = 250 //
        binding.weightValuePicker.value = 65 // Giá trị mặc định 155cm
        binding.weightValuePicker.wrapSelectorWheel = false


        // Thiết lập NumberPicker cho đơn vị
        binding.weightUnitPicker.minValue = 0
        binding.weightUnitPicker.maxValue = heightUnits.size - 1
        binding.weightUnitPicker.displayedValues = heightUnits
        binding.weightUnitPicker.value = 0 // Mặc định chọn "cm" (index 0)
        binding.weightUnitPicker.wrapSelectorWheel = false


        // Tùy chỉnh giao diện NumberPicker


        binding.weightUnitPicker.setOnValueChangedListener(OnValueChangeListener { picker, oldVal, newVal ->
            if (newVal == 0) { // kg
                binding.weightValuePicker.minValue = 30
                binding.weightValuePicker.maxValue = 250
                binding.weightValuePicker.value = 65 // Luôn set về 65 khi chọn kg
            } else { // lbs
                binding.weightValuePicker.minValue = 60
                binding.weightValuePicker.maxValue = 560
                binding.weightValuePicker.value = 143 // Luôn set về 143 khi chọn lbs
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        
        binding.btnSave.setOnClickListener {
            val selectedHeight = binding.weightValuePicker.value
            val selectedUnit = heightUnits[binding.weightUnitPicker.value]
            
            val result = Bundle().apply {
                putInt("selected_weight", selectedHeight)
                putString("selected_unit", selectedUnit)
            }
            parentFragmentManager.setFragmentResult("weight_key", result)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 