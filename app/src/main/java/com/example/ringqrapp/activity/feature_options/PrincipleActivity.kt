package com.example.ringqrapp.activity.feature_options

import android.nfc.Tag
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ringqrapp.R
import com.example.ringqrapp.adapter.FaqAdapter
import com.example.ringqrapp.adapter.TagAdapter
import com.example.ringqrapp.constant.FunctionGlobal
import com.example.ringqrapp.databinding.ActivityPrincipleBinding
import com.example.ringqrapp.utils.LastItemDividerDecoration

class PrincipleActivity : AppCompatActivity() {
    private lateinit var principleBinding : ActivityPrincipleBinding
    private lateinit var tagAdapter: TagAdapter
    private lateinit var mListDeviceAndroid : List<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        principleBinding = ActivityPrincipleBinding.inflate(layoutInflater)
        setContentView(principleBinding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, principleBinding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        getDataFaq()
        setUpViews()
        initFeatures()
    }
    private fun getDataFaq()
    {
        mListDeviceAndroid = FunctionGlobal.listMobile(this@PrincipleActivity)
    }
    private fun  setUpViews()
    {
        val linearLayoutManager = LinearLayoutManager(this@PrincipleActivity)
        val dividerItemDecoration = resources.getDrawable(R.drawable.divider_white, null )
        val customDivider = LastItemDividerDecoration(dividerItemDecoration)

        tagAdapter = TagAdapter(mListDeviceAndroid)
        principleBinding.apply {
            this.rcvDevice.layoutManager = linearLayoutManager
            this.rcvDevice.setHasFixedSize(false)
            this.rcvDevice.addItemDecoration(customDivider)
            this.rcvDevice.adapter = tagAdapter
        }
    }
    private fun initFeatures()
    {
        principleBinding.imgBack.setOnClickListener {
            this.finishAfterTransition()
        }
    }
}