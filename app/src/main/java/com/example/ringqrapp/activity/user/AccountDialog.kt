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
import com.example.ringqrapp.databinding.DialogEditAccountBinding
import com.example.ringqrapp.databinding.DialogEditHeightBinding
import com.example.ringqrapp.databinding.DialogEditWeightBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AccountDialog : BottomSheetDialogFragment() {

    private var _binding: DialogEditAccountBinding? = null
    private val binding get() = _binding!!


    // Mảng đơn vị
    private val heightUnits = arrayOf("kg", "lbs")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        // Thiết lập padding để tăng khoảng cách
    }


    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 