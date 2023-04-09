package com.hbeonlabs.driversalerts.ui.fragment.notification

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class NotificationTabAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {

    private val TAB_COUNT = 2

    override fun getItemCount(): Int {
        return TAB_COUNT
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> WarningsFragment()
            1 -> LogsFragment()
            else -> throw RuntimeException("Invalid position: $position")
        }
    }
}