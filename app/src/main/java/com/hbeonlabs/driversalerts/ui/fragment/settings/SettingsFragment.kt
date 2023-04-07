package com.hbeonlabs.driversalerts.ui.fragment.settings

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.hbeonlabs.driversalerts.R

import com.hbeonlabs.driversalerts.databinding.FragmentSettingsBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment


class SettingsFragment : BaseFragment<FragmentSettingsBinding>(){

    override fun initView() {
        super.initView()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_settings
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        val titleView = view.findViewById<TextView>(R.id.title_frag)
        val setuptextView = view.findViewById<TextView>(R.id.setup);


        titleView.text = "Settings"

      setuptextView.setOnClickListener {
          val navController = NavHostFragment.findNavController(requireParentFragment())
          navController.navigate(R.id.settingsConfigureFragment);

        }

        return view
    }
}