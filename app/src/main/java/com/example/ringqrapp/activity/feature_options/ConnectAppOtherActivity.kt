package com.example.ringqrapp.activity.feature_options

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ringqrapp.R
import com.example.ringqrapp.databinding.ActivityConnectAppOtherBinding

class ConnectAppOtherActivity : AppCompatActivity() {
    private lateinit var appOtherBinding:ActivityConnectAppOtherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appOtherBinding = ActivityConnectAppOtherBinding.inflate(layoutInflater)
        setContentView(appOtherBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}