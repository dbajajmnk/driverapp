package com.hbeonlabs.driversalerts.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentHomeBinding

class HomeActivity : AppCompatActivity() , View.OnClickListener{

    lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_home)
        binding.btnDriver.setOnClickListener(this)
        binding.btnAdmin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_driver->{
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.btn_admin->{
                startActivity(Intent(this, AdminActivity::class.java))
            }
        }
    }
}