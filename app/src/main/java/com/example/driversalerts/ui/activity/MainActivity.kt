package com.example.driversalerts.ui.activity

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.driversalerts.R
import com.example.driversalerts.databinding.ActivityMainBinding
import com.example.driversalerts.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private lateinit var navController: NavController

    override fun getLayoutResourceId(): Int {
       return R.layout.activity_main
    }

    override fun initView() {
        super.initView()

        // ======== Connecting Nav graph to Fragment
        val navHostFrag = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFrag.navController


    }
}