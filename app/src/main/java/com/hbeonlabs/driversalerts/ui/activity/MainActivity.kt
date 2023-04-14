package com.hbeonlabs.driversalerts.ui.activity

import android.content.Intent
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityMainBinding
import com.hbeonlabs.driversalerts.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController
    var count = 0;

    override fun getLayoutResourceId(): Int {

        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()

        val navHostFrag = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFrag.navController

        binding.bottomNav.setOnItemSelectedListener {

            count = 0
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

    override fun onBackPressed() {
        if(count == 0) {
            binding.bottomNav.selectedItemId = R.id.camera_menu;
            navController.navigate(R.id.cameraFragment)
        }else{
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        count += 1
    }
}