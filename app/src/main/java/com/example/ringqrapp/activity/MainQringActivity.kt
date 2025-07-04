package com.example.ringqrapp.activity

import android.app.Dialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowCompat
import com.example.ringqrapp.R
import com.example.ringqrapp.activity.target.TargetActivity
import com.example.ringqrapp.activity.user.InfoUserActivity
import com.example.ringqrapp.constant.TransitionHelper
import com.example.ringqrapp.databinding.ActivityMainQringBinding
import com.example.ringqrapp.fragment.ActivityFragment
import com.example.ringqrapp.fragment.HomeFragment
import com.example.ringqrapp.fragment.ProfileFragment
import com.example.ringqrapp.fragment.SleepFragment

class MainQringActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainQringBinding
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        binding = ActivityMainQringBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Hide the system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.fragment_container, HomeFragment())
        fragmentTransition.commit()


        setNavigationView()
        eventNavigationView()
        eventBottomNavigationView()
    }
    private fun setNavigationView() {
        binding.navigationViewApp.itemIconTintList = null
        binding.bottomNavigationView.background = null
        //binding.bottomNavigationView.itemIconTintList = null
    }

    private fun eventNavigationView() {
        binding.imgOpenNavigationApp.setOnClickListener {
            binding.main.openDrawer(GravityCompat.START)
        }
        binding.navigationViewApp.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_info_user -> {
                    TransitionHelper.navigateWithTransition(
                        this@MainQringActivity,
                        InfoUserActivity::class.java,
                        binding.main,
                        "transition_drawer",
                        R.anim.slide_in_right,
                        R.anim.slide_no_anim
                    )
                    true
                }
                R.id.nav_target -> {
                    TransitionHelper.navigateWithTransition(
                        this@MainQringActivity,
                        TargetActivity::class.java,
                        binding.main,
                        "transition_drawer",
                        R.anim.slide_in_right,
                        R.anim.slide_no_anim
                    )
                    true
                }
                R.id.nav_manager_tag ->
                {
                    TransitionHelper.navigateWithTransition(
                        this@MainQringActivity,
                        ManagerTagActivity::class.java,
                        binding.main,
                        "transition_drawer",
                        R.anim.slide_in_right,
                        R.anim.slide_no_anim
                    )
                    true
                }
                R.id.nav_clear_memory -> {
                    // Handle settings action
                    binding.main.closeDrawer(GravityCompat.START)
                    true
                }
                else -> false
            }
        }
    }
    private fun eventBottomNavigationView()
    {
       binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val fragmentTransition = supportFragmentManager.beginTransaction()
                    fragmentTransition.replace(R.id.fragment_container, HomeFragment())
                    fragmentTransition.commit()
                    true
                }
                R.id.nav_activity -> {
                    val fragmentTransition = supportFragmentManager.beginTransaction()
                    fragmentTransition.replace(R.id.fragment_container, ActivityFragment())
                    fragmentTransition.commit()
                    true
                }
                R.id.nav_sleep -> {
                    // Handle settings action
                    val fragmentTransition = supportFragmentManager.beginTransaction()
                    fragmentTransition.replace(R.id.fragment_container, SleepFragment())
                    fragmentTransition.commit()
                    true
                }
                R.id.nav_profile -> {
                    val fragmentTransition = supportFragmentManager.beginTransaction()
                    fragmentTransition.replace(R.id.fragment_container, ProfileFragment())
                    fragmentTransition.commit()
                    true
                }
                else -> false
            }
       }
    }
}