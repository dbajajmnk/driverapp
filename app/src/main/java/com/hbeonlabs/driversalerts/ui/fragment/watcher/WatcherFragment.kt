package com.hbeonlabs.driversalerts.ui.fragment.watcher

import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentWatcherBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper

class WatcherFragment : BaseFragment<FragmentWatcherBinding>(){

    override fun initView() {
        super.initView()
        val webRtcHelper = WebRtcHelper()
        webRtcHelper.start(requireContext(), null,binding.surfaceview)
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_watcher
    }

}