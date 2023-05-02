package com.hbeonlabs.driversalerts.ui.fragment.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentSettingsConfigureBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.notification.WarningViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsConfigureFragment : BaseFragment<FragmentSettingsConfigureBinding>() {

    private val viewModel: SettingsViewModel by viewModels()
    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_settings_configure;
    }

    override fun initView() {
        super.initView()
        binding.include4.titleFrag.text = "Settings"
        binding.button.setOnClickListener {
            viewModel.addDeviceConfiguration(
                binding.licenseKey.text.toString(),
                binding.deviceId.text.toString(),
                binding.vehicleId.text.toString(),
                binding.bluetoothId.text.toString()
            )
        }


    }

    override fun observe() {
        super.observe()
        viewModel.showProgressBarLiveData.observe(this) {
            if (it)
                binding.progressBar.visibility = View.VISIBLE
            else
                binding.progressBar.visibility = View.GONE
        }
    }
}