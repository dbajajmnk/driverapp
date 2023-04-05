package com.hbeonlabs.driversalerts.ui.fragment.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentHistoryBinding
import com.hbeonlabs.driversalerts.databinding.FragmentWatcherBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.notification.WarningsFragment
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper

class HistoryFragment : BaseFragment<FragmentHistoryBinding>(){

    override fun initView() {
        super.initView()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_history
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val TitleView = view.findViewById<TextView>(R.id.title_frag)
        TitleView.text = "History"
        val childFragment = HistoryDatesFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment_container, childFragment)
            .commit()

        return view
    }
}