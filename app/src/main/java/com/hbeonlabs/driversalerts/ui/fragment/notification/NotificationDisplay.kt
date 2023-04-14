package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
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

        val ok = view.findViewById<Button>(R.id.ok_btn)
        val TitleView = view.findViewById<TextView>(R.id.title_frag)
        val navController = NavHostFragment.findNavController(requireParentFragment())

        TitleView.text = "Notification"

        ok.setOnClickListener { navController.navigate(R.id.notificationsFragment); }

        return view
    }

}