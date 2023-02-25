package com.hbeonlabs.driversalerts.ui.activity

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityMainBinding
import com.hbeonlabs.driversalerts.ui.activity.setting.SettingsActivity
import com.hbeonlabs.driversalerts.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController
    /*companion object {
        init {
            System.loadLibrary("jingle_peerconnection_so")
        }
    }*/
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        super.initView()

        // ======== Connecting Nav graph to Fragment
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFrag.navController


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.setting_menu) {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}