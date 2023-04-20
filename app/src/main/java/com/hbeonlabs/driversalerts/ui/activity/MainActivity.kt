package com.hbeonlabs.driversalerts.ui.activity

import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityMainBinding
import com.hbeonlabs.driversalerts.ui.base.BaseActivity
import com.hbeonlabs.driversalerts.utils.batteryChargingStatusChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController

    override fun getLayoutResourceId(): Int {

        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()



        val navHostFrag = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFrag.navController

        binding.bottomNav.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.camera_menu -> {
                    navController.navigate(R.id.dashboardFragment)
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

                R.id.attendance_menu ->{
                    navController.navigate(R.id.attendancedatesFragment)
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
}