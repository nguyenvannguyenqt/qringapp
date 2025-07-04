package com.example.ringqrapp.activity.user

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ringqrapp.R
import com.example.ringqrapp.databinding.DialogEditGenderBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenderDialog : BottomSheetDialogFragment() {

    private var _binding: DialogEditGenderBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogEditGenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtSave.setOnClickListener {
            val selectedGender = when (binding.rgGender.checkedRadioButtonId) {
                R.id.rb_male -> getString(R.string.sex_male)
                R.id.rb_female -> getString(R.string.sex_female)
                else -> ""
            }
            parentFragmentManager.setFragmentResult("gender_key", Bundle().apply { putString("selected_gender", selectedGender) })
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 