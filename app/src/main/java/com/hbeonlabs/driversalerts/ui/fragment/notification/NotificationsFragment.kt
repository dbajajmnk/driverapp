package com.hbeonlabs.driversalerts.ui.fragment.notification

import com.google.android.material.tabs.TabLayoutMediator
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentNotificationBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<FragmentNotificationBinding>(){


    override fun initView() {
        super.initView()
        binding.include.titleFrag.text = "Notifications"
        initTabLayout()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_notification

    }

    private fun initTabLayout() {
        val tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.vpNotifications) { tab, position ->
                when (position) {
                    0 -> tab.text = "Warnings"
                    1 -> tab.text = "Logs"
                }
            }
        binding.vpNotifications.adapter = NotificationTabAdapter(childFragmentManager,lifecycle)
        tabLayoutMediator.attach()
    }



}