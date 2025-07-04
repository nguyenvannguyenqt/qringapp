package com.example.ringqrapp.activity.user

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.example.ringqrapp.R
import com.example.ringqrapp.activity.MainQringActivity
import com.example.ringqrapp.activity.register.SignInActivity
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.ActivityInfoUserBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InfoUserActivity : AppCompatActivity() {
    private lateinit var infoBinding:ActivityInfoUserBinding
    private var loadingDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        infoBinding = ActivityInfoUserBinding.inflate(layoutInflater)
        setContentView(infoBinding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, infoBinding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        initFeatures()

        supportFragmentManager.setFragmentResultListener("gender_key", this) { _, bundle ->
            val selectedGender = bundle.getString("selected_gender")
            infoBinding.tvGenderValue.text = selectedGender
        }

        supportFragmentManager.setFragmentResultListener("nickname_key", this) { requestKey, bundle ->
            val selectedNickname = bundle.getString("selected_nickname")
            infoBinding.tvNicknameValue.text = selectedNickname
        }

        supportFragmentManager.setFragmentResultListener("birthdate_key", this) { _, bundle ->
            val year = bundle.getInt("selected_year")
            val month = bundle.getInt("selected_month")
            infoBinding.tvBirthdateValue.text = String.format("%04d-%02d", year, month)
        }
        supportFragmentManager.setFragmentResultListener("height_key", this) { _, bundle ->
            val height = bundle.getInt("selected_height")
            val unit = bundle.getString("selected_unit")
            infoBinding.tvHeightValue.text = String.format("%d %s", height, unit)
        }
        supportFragmentManager.setFragmentResultListener("weight_key", this) { _, bundle ->
            val weight = bundle.getInt("selected_weight")
            val unit = bundle.getString("selected_unit")
            infoBinding.tvWeightValue.text = String.format("%d %s", weight, unit)
        }
    }
    private  fun initFeatures(){
        infoBinding.imgBack.setOnClickListener {
            TransitionHelper.navigateWithTransition(
                this@InfoUserActivity,
                MainQringActivity::class.java,
                infoBinding.main,
                "transition_drawer",
                R.anim.slide_in_left,
                R.anim.slide_no_anim
            )
            this.finish()
        }

        infoBinding.tvSave.setOnClickListener {

        }
        infoBinding.layoutGender.setOnClickListener {
            val editGenderDialog = GenderDialog()
            editGenderDialog.show(supportFragmentManager, editGenderDialog.tag)
        }
        infoBinding.layoutNickname.setOnClickListener {
            val editNicknameDialog = NicknameDialog()
            editNicknameDialog.show(supportFragmentManager, editNicknameDialog.tag)
        }
        infoBinding.layoutBirthdate.setOnClickListener {
            val editBirthdateDialog = BirthdateDialog()
            editBirthdateDialog.show(supportFragmentManager, editBirthdateDialog.tag)
        }
        infoBinding.layoutHeight.setOnClickListener {
            val editHeightDialog = HeightDialog()
            editHeightDialog.show(supportFragmentManager, editHeightDialog.tag)
        }
        infoBinding.layoutWeight.setOnClickListener {
            val editWeightDialog = WeightDialog()
            editWeightDialog.show(supportFragmentManager, editWeightDialog.tag)
        }
        infoBinding.layoutChangeAccount.setOnClickListener {
            val accountDialog = AccountDialog()
            accountDialog.show(supportFragmentManager, accountDialog.tag)
        }

        infoBinding.btnLogout.setOnClickListener {
            showDialog(true)
            lifecycleScope.launch(Dispatchers.IO) {
                FirebaseAuth.getInstance().signOut()
                withContext(Dispatchers.Main)
                {
                    //DataStoreManager.setUser(null)
                    startActivity(Intent(this@InfoUserActivity, SignInActivity::class.java))
                    finishAffinity()
                    showDialog(false)
                }
            }
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