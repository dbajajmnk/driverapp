package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentWarningsBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment


class NotificationDisplay : BaseFragment<FragmentWarningsBinding>() {

    override fun initView() {
        super.initView()
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_notification_display

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification_display, container, false)
        Log.d("MyApp","Hi")
        return view
    }

}