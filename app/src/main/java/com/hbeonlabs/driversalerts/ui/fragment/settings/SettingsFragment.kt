package com.hbeonlabs.driversalerts.ui.fragment.settings

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R

import com.hbeonlabs.driversalerts.databinding.FragmentSettingsBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(){
    private val viewModel: SettingsViewModel by viewModels()

    override fun initView() {
        super.initView()

        binding.include.titleFrag.text = "Settings"
        binding.setup.setOnClickListener {
            val navController = NavHostFragment.findNavController(requireParentFragment())
            navController.navigate(R.id.settingsConfigureFragment);

        }
        val deviceData = viewModel.getDeviceConfiguration()
        if (deviceData!=null)
        {
            binding.apply {
                vehicleId.text = deviceData.vehicleId.toString()
                deviceId.text = deviceData.deviceId
                bluetoothId.text = ""
                ivDeviceStatus.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.device_status_configured))
            }
        }
        else{
            binding.ivDeviceStatus.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.device_status_not_configured))
        }

    }



    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_settings
    }

}