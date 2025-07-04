package com.example.ringqrapp.activity.target

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.NumberPicker.OnValueChangeListener
import android.widget.Toast
import com.example.ringqrapp.R
import com.example.ringqrapp.databinding.DialogDistanceBinding
import com.example.ringqrapp.databinding.DialogEditAccountBinding
import com.example.ringqrapp.databinding.DialogEditHeightBinding
import com.example.ringqrapp.databinding.DialogEditWeightBinding
import com.example.ringqrapp.databinding.DialogNumberStepBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DistanceDialog : BottomSheetDialogFragment() {

    private var _binding: DialogDistanceBinding? = null
    private val binding get() = _binding!!


    // Mảng đơn vị

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogDistanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupNumberPicker()
        // Thiết lập padding để tăng khoảng cách
    }
    private fun setupNumberPicker() {

        binding.numberPickerDistance.minValue = 1 // Chiều cao tối thiểu 50cm
        binding.numberPickerDistance.maxValue = 100 // Chiều cao tối đa 250cm
        binding.numberPickerDistance.value = 5 // Giá trị mặc định 155cm
        binding.numberPickerDistance.wrapSelectorWheel = false

        disableManualInput(binding.numberPickerDistance) // Ngăn chỉnh sửa

        binding.numberPickerDistance.setOnValueChangedListener { picker, oldVal, newVal ->
            // Xử lý sự kiện khi giá trị thay đổi 100)
        }

    }
    private fun disableManualInput(numberPicker: NumberPicker) {
        try {
            for (i in 0 until numberPicker.childCount) {
                val child = numberPicker.getChildAt(i)
                if (child is EditText) {
                    child.isFocusable = false
                    child.isClickable = false
                    child.isCursorVisible = false
                    child.inputType = InputType.TYPE_NULL
                    child.setBackgroundColor(Color.TRANSPARENT) // nếu muốn ẩn viền
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @SuppressLint("SoonBlockedPrivateApi")
    private fun customizeNumberPicker() {
        // Set divider color to blue
        val pickerFields = NumberPicker::class.java.declaredFields
        for (field in pickerFields) {
            if (field.name == "mSelectionDivider") {
                field.isAccessible = true
                try {
                    val colorDrawable = ColorDrawable(Color.parseColor("#4A90E2"))
                    field.set(binding.numberPickerDistance, colorDrawable)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                break
            }
        }

        // Set text color for selected item
        try {
            val selectorWheelPaintField = binding.numberPickerDistance.javaClass
                .getDeclaredField("mSelectorWheelPaint")
            selectorWheelPaintField.isAccessible = true
            (selectorWheelPaintField.get(binding.numberPickerDistance) as Paint)
                .color = Color.parseColor("#4A90E2")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val selectedDistance = binding.numberPickerDistance.value
            println("selectedDistance = $selectedDistance")
            val result = Bundle().apply {
                putInt("selected_distance", selectedDistance)
            }
            parentFragmentManager.setFragmentResult("distance_key", result)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 