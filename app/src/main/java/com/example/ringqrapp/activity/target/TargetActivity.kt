package com.example.ringqrapp.activity.target

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ringqrapp.R
import com.example.ringqrapp.activity.MainQringActivity
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.ActivityTargetBinding

class TargetActivity : AppCompatActivity() {
    private lateinit var targetBinding: ActivityTargetBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        targetBinding = ActivityTargetBinding.inflate(layoutInflater)
        setContentView(targetBinding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, targetBinding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        supportFragmentManager.setFragmentResultListener("step_key", this) { _, bundle ->
            val selectedStep = bundle.getInt("selected_step")
            targetBinding.tvNumberStepValue.text = "${selectedStep} bước"
        }
        supportFragmentManager.setFragmentResultListener("distance_key", this) { _, bundle ->
            val selectedStep = bundle.getInt("selected_distance")
            targetBinding.tvDistanceValue.text = "$selectedStep ${resources.getString(R.string.km)}"
        }
        supportFragmentManager.setFragmentResultListener("calo_key", this) { _, bundle ->
            val selectedStep = bundle.getInt("selected_calo")
            targetBinding.tvKcalValue.text = "$selectedStep ${resources.getString(R.string.kcal)}"
        }
        initFeatures()
    }
    private fun initFeatures(){
        targetBinding.imgBack.setOnClickListener {
            TransitionHelper.navigateWithTransition(
                this@TargetActivity,
                MainQringActivity::class.java,
                targetBinding.main,
                "transition_drawer",
                R.anim.slide_in_left,
                R.anim.slide_no_anim
            )
            this.finish()
        }

        targetBinding.layoutStep.setOnClickListener {
            val stepDialog = StepDialog()
            stepDialog.show(supportFragmentManager, stepDialog.tag)
        }

        targetBinding.layoutDistance.setOnClickListener {
            val distanceDialog = DistanceDialog()
            distanceDialog.show(supportFragmentManager, distanceDialog.tag)
        }

        targetBinding.layoutCalo.setOnClickListener {
            val caloDialog = CaloDialog()
            caloDialog.show(supportFragmentManager, caloDialog.tag)
        }
    }
}