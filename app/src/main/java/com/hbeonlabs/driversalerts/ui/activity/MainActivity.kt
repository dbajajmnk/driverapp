package com.hbeonlabs.driversalerts.ui.activity

import android.content.Context
import android.os.PowerManager
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityMainBinding
import com.hbeonlabs.driversalerts.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController
    private lateinit var wakeLock: PowerManager.WakeLock
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

                R.id.attendance_menu -> {
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
        aquireWakeLock()
    }

    private fun aquireWakeLock() {
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DriverApp:VideoStreaming").apply {
                    acquire()
                }
            }
    }

    private fun releaseWakeLock() {
        if (this::wakeLock.isInitialized) {
            wakeLock.release()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseWakeLock()
    }
}
