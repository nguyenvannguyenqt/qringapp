package com.example.ringqrapp.activity.register

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
import com.example.ringqrapp.constant.FunctionGlobal.isPasswordValid
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        initFeatures()
    }
    private fun initFeatures(){
       binding.btnBack.setOnClickListener {
           this.finish()
       }
       binding.btnSignUp.setOnClickListener {
           registerAccount()
       }
       binding.btnSignIn.setOnClickListener {
           backSignInActivity()
       }
    }

    private fun checkData(
        email: String,
        password: String,
        confirmPassword: String,
    ): Boolean{
        if (TextUtils.isEmpty(email)) {
            binding.txtErrorInput.text = resources.getString(R.string.message_required_email)
            return false
        } else if (TextUtils.isEmpty(password)) {
            binding.txtErrorInput.text = resources.getString(R.string.message_required_password)
            return false
        } else if (TextUtils.isEmpty(confirmPassword)) {
            binding.txtErrorInput.text = resources.getString(R.string.message_required_confirm_password)
            return false
        }
        return true
    }
    private fun AppCompatActivity.showMessage(strMessage: String) {
        Toast.makeText(this@SignUpActivity, strMessage, Toast.LENGTH_SHORT).show()
    }

    private fun registerAccount() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

        if (!checkData(email, password, confirmPassword)) {
            return
        }

        if (!FunctionGlobal.isEmailValid2(email)) {
            binding.txtErrorInput.text = resources.getString(R.string.text_invalid_email)
            return
        }

        if (!isPasswordValid(password)) {
            binding.txtErrorInput.text = resources.getString(R.string.text_invalid_password)
            return
        }

        if (password != confirmPassword) {
            binding.txtErrorInput.text = resources.getString(R.string.text_password_not_match)
            return
        }

        showDialog(true)
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                        firebaseFirestore.collection("users")
//                            .document(firebaseAuth.currentUser!!.uid)
//                            .set(mapOf("email" to email))
//                            .addOnSuccessListener {
//                                backSignInActivity()
//                            }
//                            .addOnFailureListener { e ->
//                                e.printStackTrace()
//                                showMessage(resources.getString(R.string.text_error_register))
//                            }
                        showMessage("Đăng ký thành công, vui lòng đăng nhập")
                    } else {
                        binding.txtErrorInput.text = resources.getString(R.string.text_email_exists)
                    }
                    showDialog(false)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            showMessage(resources.getString(R.string.text_error_register))
            showDialog(false)
        } finally {
            FunctionGlobal.hideSoftKeyboard(this@SignUpActivity)
        }
    }
    private fun backSignInActivity() {
        resetData()
        TransitionHelper.navigateWithTransition(
            this@SignUpActivity,
            SignInActivity::class.java,
            binding.main,
            "transition_register",
            R.anim.slide_in_left,
            R.anim.slide_no_anim
        )
    }
    private fun resetData() {
        binding.let {
            it.edtEmail.setText("")
            it.edtPassword.setText("")
            it.edtConfirmPassword.setText("")
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