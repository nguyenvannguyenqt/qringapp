package com.example.ringqrapp.activity.user

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ringqrapp.R
import com.example.ringqrapp.databinding.DialogEditBirthdayBinding
import com.example.ringqrapp.databinding.DialogEditGenderBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BirthdateDialog : BottomSheetDialogFragment() {

    private var _binding: DialogEditBirthdayBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogEditBirthdayBinding.inflate(inflater, container, false)
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
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        binding.yearPicker.minValue = 1900
        binding.yearPicker.maxValue = currentYear
        binding.yearPicker.value = 2003 // Default value
        binding.yearPicker.wrapSelectorWheel = false

        val months = Array(12) { i -> String.format("%02d", i + 1) }
        binding.monthPicker.minValue = 1
        binding.monthPicker.maxValue = 12
        binding.monthPicker.displayedValues = months
        binding.monthPicker.value = 8 // Default to August
        binding.monthPicker.wrapSelectorWheel = false
    }

    private fun setupClickListeners() {
        binding.root.findViewById<View>(R.id.btn_cancel).setOnClickListener {
            dismiss()
        }
        binding.root.findViewById<View>(R.id.btn_save).setOnClickListener {
            val selectedYear = binding.yearPicker.value
            val selectedMonth = binding.monthPicker.value
            val result = Bundle().apply {
                putInt("selected_year", selectedYear)
                putInt("selected_month", selectedMonth)
            }
            parentFragmentManager.setFragmentResult("birthdate_key", result)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 