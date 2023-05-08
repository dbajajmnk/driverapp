package com.hbeonlabs.driversalerts.ui.fragment.history

import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentHistoryBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment


class HistoryFragment : BaseFragment<FragmentHistoryBinding>(){

    override fun initView() {
        super.initView()
        binding.include.titleFrag.text = "History"
        val childFragment = HistoryDatesFragment()

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment_container, childFragment)
            .commit()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_history
    }
}