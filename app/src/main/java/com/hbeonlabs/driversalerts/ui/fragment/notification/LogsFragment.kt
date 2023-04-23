package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentLogsBinding
import com.hbeonlabs.driversalerts.databinding.FragmentNotificationBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.utils.collectLatestLifeCycleFlow
import com.hbeonlabs.driversalerts.utils.makeToast
import javax.inject.Inject

class LogsFragment : BaseFragment<FragmentLogsBinding>(){

    @Inject
    lateinit var itemAdapter: NotificationAdapter

    private val viewModel:WarningViewModel by viewModels()

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_logs
    }


    override fun initView() {
        super.initView()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
    }


    override fun observe() {
        super.observe()
        collectLatestLifeCycleFlow(viewModel.notificationEvent)
        {
            when(it){
                is WarningViewModel.NotificationEvents.ErrorEvent -> {
                    makeToast(it.message)
                }
                is WarningViewModel.NotificationEvents.LoadingEvent -> {
                    if (it.isLoading)
                    {

                    }
                    else{

                    }
                }
                is WarningViewModel.NotificationEvents.NotificationListEvents -> {
                    itemAdapter.differ.submitList(it.notifications)
                }
            }
        }
    }


}