package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.util.Log
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentHistoryBinding
import com.hbeonlabs.driversalerts.databinding.FragmentNotificationBinding
import com.hbeonlabs.driversalerts.databinding.FragmentWatcherBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper

class NotificationsFragment : BaseFragment<FragmentNotificationBinding>(){

    override fun initView() {
        super.initView()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_notification
    }
}