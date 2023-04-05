package com.hbeonlabs.driversalerts.ui.fragment.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentSettingsConfigureBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment


class SettingsConfigureFragment : BaseFragment<FragmentSettingsConfigureBinding>() {

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_settings_configure;
    }

    override fun initView() {
        super.initView()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings_configure, container, false)
        return view;
    }

}