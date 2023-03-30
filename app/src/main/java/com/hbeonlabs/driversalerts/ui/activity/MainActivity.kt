package com.hbeonlabs.driversalerts.ui.activity

import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityMainBinding
import com.hbeonlabs.driversalerts.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController

    override fun getLayoutResourceId(): Int {

        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()

        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFrag.navController

        val toolbar = findViewById<Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)

        binding.bottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.camera_menu -> {
                    navController.navigate(R.id.cameraFragment)
                    true
                }

                R.id.history_menu -> {
                    navController.navigate(R.id.historyFragment)
                    true
                }
                R.id.notification_menu -> {
                    navController.navigate(R.id.notificationsFragment)
                    true
                }
                R.id.setting_menu -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> {
                    false
                }
            }

        }

    }

    override fun onBackPressed() {
        navController.navigate(R.id.cameraFragment)
    }
}