package com.example.ringqrapp.activity.register

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ringqrapp.MainActivity
import com.example.ringqrapp.R
import com.example.ringqrapp.activity.MainQringActivity
import com.example.ringqrapp.constant.FunctionGlobal
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        initFeatures()
    }
    private fun initFeatures(){
        binding.btnBack.setOnClickListener {
            this.finish()
        }
        binding.btnSignIn.setOnClickListener {
            loginAccount()
        }
        binding.btnSignUp.setOnClickListener {
            backSignUpActivity()
        }
        binding.tvForgotPassword.setOnClickListener {
            handleForgotPassword()
        }
    }

    private fun checkData(
        email: String,
        password: String,
    ): Boolean{
        if (TextUtils.isEmpty(email)) {
            binding.txtErrorInput.text = resources.getString(R.string.message_required_email)
            return false
        } else if (TextUtils.isEmpty(password)) {
            binding.txtErrorInput.text = resources.getString(R.string.message_required_password)
            return false
        }
        return true
    }
    private fun AppCompatActivity.showMessage(strMessage: String) {
        Toast.makeText(this@SignInActivity, strMessage, Toast.LENGTH_SHORT).show()
    }


    private fun loginAccount() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        if (!checkData(email, password)) {
            return
        }

        if (!FunctionGlobal.isEmailValid2(email)) {
            binding.txtErrorInput.text = resources.getString(R.string.text_invalid_email)
            return
        }


        showDialog(true)
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        resetData()
                        TransitionHelper.navigateWithTransition(
                            this@SignInActivity,
                            MainQringActivity::class.java,
                            binding.main,
                            "transition_login",
                            R.anim.slide_in_right,
                            R.anim.slide_no_anim
                        )
                    } else {
                        showMessage(resources.getString(R.string.text_invalid_login_info))
                    }
                    showDialog(false)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage(resources.getString(R.string.text_error_login))
            showDialog(false)
        } finally {
            FunctionGlobal.hideSoftKeyboard(this@SignInActivity)
        }
    }

    private fun backSignUpActivity() {
        resetData()
        TransitionHelper.navigateWithTransition(
            this@SignInActivity,
            SignUpActivity::class.java,
            binding.main,
            "transition_register",
            R.anim.slide_in_right,
            R.anim.slide_no_anim
        )
    }
    private fun handleForgotPassword(){
        TransitionHelper.navigateWithTransition(
            this@SignInActivity,
            ForgotPasswordActivity::class.java,
            binding.main,
            "transition_register",
            R.anim.slide_in_right,
            R.anim.slide_no_anim
        )
    }
    private fun resetData() {
        binding.let {
            it.edtEmail.setText("")
            it.edtPassword.setText("")
        }
    }
    private fun showDialog(isTrue: Boolean) {

        if (loadingDialog == null) {
            loadingDialog = Dialog(this)
            loadingDialog!!.setContentView(R.layout.dialog_loading)
            loadingDialog!!.setCancelable(false)
        }

        if (isTrue) {
            loadingDialog!!.show()
        } else {
            loadingDialog!!.dismiss()
        }
    }
    override fun onPause() {
        super.onPause()
        loadingDialog?.dismiss()
    }
}