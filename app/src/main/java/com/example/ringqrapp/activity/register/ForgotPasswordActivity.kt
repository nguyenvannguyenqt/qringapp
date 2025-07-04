package com.example.ringqrapp.activity.register

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ringqrapp.R
import com.example.ringqrapp.constant.FunctionGlobal
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var dialog: Dialog
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgBack.setOnClickListener {
            handleBackPress()
        }

        binding.btnSendEmail.setOnClickListener {
            handleResetPasswordByEmail()
        }

    }

    private fun checkData(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            binding.txtErrorInput.text = resources.getString(R.string.message_required_email)
            return false
        } else if (!FunctionGlobal.isEmailValid2(email)) {
            binding.txtErrorInput.text = resources.getString(R.string.text_invalid_email)
            return false
        }
        return true
    }

    private fun handleBackPress() {
        TransitionHelper.navigateWithTransition(
            this@ForgotPasswordActivity,
            SignInActivity::class.java,
            binding.main,
            "transition_register",
            R.anim.slide_in_left,
            R.anim.slide_no_anim
        )
        finish()
    }

    private fun handleResetPasswordByEmail() {
        val strEmail = binding.edtEmail.text.toString().trim()
        if (checkData(strEmail)) {
            resetPassword(strEmail)
        }
    }

    private fun resetPassword(email: String) {
        showDialog(true)
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                showDialog(false)
                if (task.isSuccessful) {
                    showMessage(resources.getString(R.string.text_reset_password_success))
                    handleBackPress()
                } else {
                    val errorCode = (task.exception as? FirebaseAuthException)?.errorCode
                    if (errorCode == "ERROR_USER_NOT_FOUND") {
                        binding.txtErrorInput.text = resources.getString(R.string.text_email_not_registered)
                    } else {
                        binding.txtErrorInput.text = resources.getString(R.string.text_reset_password_failed)
                    }
                }
            }
            .addOnFailureListener { exception ->
                showDialog(false)
                if (exception is FirebaseAuthInvalidUserException) {
                    binding.txtErrorInput.text = resources.getString(R.string.text_email_not_registered)
                } else {
                    binding.txtErrorInput.text = resources.getString(R.string.text_reset_password_failed)
                }
            }
    }

    private fun Activity.showMessage(message: String) {
        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDialog(isTrue: Boolean) {
        if (isTrue)
            dialog.show()
        else
            dialog.dismiss()
    }
}