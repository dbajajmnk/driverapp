package com.hbeonlabs.driversalerts.ui.activity

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityMainBinding
import com.hbeonlabs.driversalerts.ui.base.BaseActivity
import com.hbeonlabs.driversalerts.ui.fragment.camera.CameraFragment
import com.hbeonlabs.driversalerts.ui.fragment.history.HistoryFragment
import com.hbeonlabs.driversalerts.ui.fragment.notification.NotificationsFragment
import com.hbeonlabs.driversalerts.ui.fragment.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.widget.Toolbar

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController
    /*companion object {
        init {
            System.loadLibrary("jingle_peerconnection_so")
        }
    }*/

//    private lateinit var myToolbar: Toolbar

    override fun getLayoutResourceId(): Int {

        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()

        // ======== Connecting Nav graph to Fragment
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFrag.navController
//        myToolbar = findViewById(R.id.custom_toolbar);


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