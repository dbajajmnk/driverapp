package com.hbeonlabs.driversalerts.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentWatcherBinding
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper

class AdminActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<FragmentWatcherBinding>(this, R.layout.fragment_watcher)
        WebRtcHelper.getInstance().init(this)
        WebRtcHelper.getInstance().startReceiverStreaming(binding.surfaceview)
        //WebRtcHelper.getInstance().start(this, null,binding.surfaceview)
    }
}