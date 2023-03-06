package com.hbeonlabs.driversalerts.ui.fragment.home

import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentHomeBinding
import com.hbeonlabs.driversalerts.databinding.FragmentWatcherBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.shivam.androidwebrtc.tutorial.WebRtcHelper

class HomeFragment: BaseFragment<FragmentHomeBinding>(), View.OnClickListener{

    override fun initView() {
        super.initView()
        binding.btnDriver.setOnClickListener(this)
        binding.btnAdmin.setOnClickListener(this)
        binding.btnSetting.setOnClickListener(this)
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_home
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_driver->{
                findNavController().navigate(R.id.cameraFragment)
            }
            R.id.btn_admin->{
                findNavController().navigate(R.id.watcherFragment)
            }
            R.id.btn_setting->{
                Toast.makeText(context, "Go to settings.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}