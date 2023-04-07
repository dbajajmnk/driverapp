package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentNotificationBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment


class NotificationsFragment : BaseFragment<FragmentNotificationBinding>(){


    override fun initView() {
        super.initView()
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_notification

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        val TitleView = view.findViewById<TextView>(R.id.title_frag)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        TitleView.text = "Notifications"

        val childFragment = WarningsFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment_container, childFragment)
            .commit()

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val fragment: Fragment?
                when (tab.position) {
                    0 -> fragment = WarningsFragment()
                    1 -> fragment = LogsFragment()
                    else -> fragment = null
                }
                val transaction = fragmentManager!!.beginTransaction()
                if (fragment != null) {
                    transaction.replace(R.id.child_fragment_container, fragment).commit()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
               return
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                return
            }

        })
        return view
    }



}