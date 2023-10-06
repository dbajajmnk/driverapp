package com.hbeonlabs.driversalerts.ui.fragment.settings

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentSettingsConfigureBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsConfigureFragment : BaseFragment<FragmentSettingsConfigureBinding>() {

    private val viewModel: SettingsViewModel by viewModels()
    private var hasSubmittedConfigure = false
    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_settings_configure
    }

    override fun initView() {
        super.initView()
        binding.include4.titleFrag.text = "Settings"
        binding.button.setOnClickListener {
            viewModel.addDeviceConfiguration(
                binding.licenseKey.text.toString(),
                binding.deviceId.text.toString(),
                binding.bluetoothId.text.toString()
            )
        }


    }

    override fun observe() {
        super.observe()
        viewModel.showProgressBarLiveData.observe(this) {
            if (it) {
                hasSubmittedConfigure = true
                binding.progressBar.visibility = View.VISIBLE
            }else {
                binding.progressBar.visibility = View.GONE
                if(hasSubmittedConfigure){
                    val navController = NavHostFragment.findNavController(requireParentFragment())
                    navController.popBackStack()
                }
            }
        }
    }
}