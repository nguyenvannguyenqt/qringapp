package com.example.ringqrapp.activity.feature_options

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
import com.example.ringqrapp.constant.FunctionGlobal
import com.example.ringqrapp.databinding.ActivityFaqQuestionBinding
import com.example.ringqrapp.model.Faq
import com.example.ringqrapp.utils.LastItemDividerDecoration

class FaqQuestionActivity : AppCompatActivity() {
    private lateinit var faqBinding: ActivityFaqQuestionBinding

    private lateinit var mListFaq: MutableList<Faq>
    private lateinit var faqAdapter: FaqAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        faqBinding = ActivityFaqQuestionBinding.inflate(layoutInflater)
        setContentView(faqBinding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, faqBinding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        getDataFaq()
        setUpViews()
        initFeatures()
    }
    private fun getDataFaq()
    {
        mListFaq = FunctionGlobal.listFaq(this@FaqQuestionActivity)
    }
    private fun  setUpViews()
    {
        val linearLayoutManager = LinearLayoutManager(this@FaqQuestionActivity)
        val dividerItemDecoration = resources.getDrawable(R.drawable.divider_white, null )
        val customDivider = LastItemDividerDecoration(dividerItemDecoration)

        faqAdapter = FaqAdapter(mListFaq)
        faqBinding.apply {
            this.rcvFaq.layoutManager = linearLayoutManager
            this.rcvFaq.setHasFixedSize(false)
            this.rcvFaq.addItemDecoration(customDivider)
            this.rcvFaq.adapter = faqAdapter
        }
    }
    private fun initFeatures()
    {
        faqBinding.imgBack.setOnClickListener {
            this.finishAfterTransition()
        }
    }
}