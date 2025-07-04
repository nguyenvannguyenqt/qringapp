package com.example.ringqrapp.activity

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
import com.example.ringqrapp.adapter.TagAdapter
import com.example.ringqrapp.constant.FunctionGlobal
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.ActivityManagerTagBinding
import com.example.ringqrapp.model.HealthItem
import com.example.ringqrapp.model.HealthItem.*
import com.example.ringqrapp.utils.LastItemDividerDecoration

class ManagerTagActivity : AppCompatActivity() {
    private lateinit var managerTagBinding:ActivityManagerTagBinding

    private lateinit var mListTags:List<String>
    private lateinit var tagAdapter:TagAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        managerTagBinding = ActivityManagerTagBinding.inflate(layoutInflater)
        setContentView(managerTagBinding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, managerTagBinding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        getData()
        initViews()
        initFeatures()
    }
    private fun getData()
    {
        mListTags = FunctionGlobal.listTag(this@ManagerTagActivity)
    }
    private fun initViews()
    {
        val dividerItemDecoration = resources.getDrawable(R.drawable.divider_white, null )
        val customDivider = LastItemDividerDecoration(dividerItemDecoration)

        tagAdapter = TagAdapter(mListTags)

        managerTagBinding.rcvTag.apply {
            val linearLayoutManager = LinearLayoutManager(this@ManagerTagActivity)
            this.layoutManager = linearLayoutManager
            this.setHasFixedSize(false)
            this.addItemDecoration(customDivider)
            this.adapter = tagAdapter
        }
    }
    private fun initFeatures()
    {
        managerTagBinding.imgBack.setOnClickListener{
            TransitionHelper.navigateWithTransition(
                this@ManagerTagActivity,
                MainQringActivity::class.java,
                managerTagBinding.main,
                "transition_drawer",
                R.anim.slide_in_left,
                R.anim.slide_no_anim
            )
            this.finish()
        }
    }
}