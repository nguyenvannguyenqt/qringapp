package com.example.ringqrapp.activity.feature_options

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ringqrapp.R
import com.example.ringqrapp.activity.register.SignInActivity
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.ActivityThemeBinding
import com.example.ringqrapp.fragment.ProfileFragment

class ThemeActivity : AppCompatActivity() {
    private lateinit var themeBinding : ActivityThemeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        themeBinding = ActivityThemeBinding.inflate(layoutInflater)
        setContentView(themeBinding.root)

        themeBinding.switchToggleDark.isChecked = true
        initFeature()
    }
    private fun initFeature()
    {
        themeBinding.imgBack.setOnClickListener {
            this.finishAfterTransition()
        }
    }
}