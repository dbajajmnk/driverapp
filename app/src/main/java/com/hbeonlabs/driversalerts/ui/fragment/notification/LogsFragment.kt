package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentLogsBinding
import com.hbeonlabs.driversalerts.databinding.FragmentNotificationBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.utils.collectLatestLifeCycleFlow
import com.hbeonlabs.driversalerts.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LogsFragment : BaseFragment<FragmentLogsBinding>(){

    @Inject
    lateinit var itemAdapter: NotificationAdapter

    private val viewModel:WarningViewModel by viewModels()

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_logs
    }


    override fun initView() {
        super.initView()
        checkPagingStates()

        binding.recyclerView.apply {
            itemAdapter = NotificationAdapter()
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
    }


    override fun observe() {
        super.observe()
        collectLatestLifeCycleFlow(viewModel.getAllNotificationsFromApi())
        {
            itemAdapter.submitData(it)
        }

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
                   // itemAdapter.differ.submitList(it.notifications)
                }
            }
        }


    }

    private fun checkPagingStates()
    {
        collectLatestLifeCycleFlow(itemAdapter.loadStateFlow){loadState->
            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error ->  loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            errorState?.let {
                makeToast(it.error.message?:"Unknown Error")
            }

            if (loadState.refresh == LoadState.Loading)
            {
                // =========== Shimmer During Only First Loading ==========

            }
            else if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && itemAdapter.itemCount < 1)
            {
                // =========== Empty List  ==========

            }
            else if (loadState.refresh is LoadState.NotLoading)
            {
                // ======== When First Loading Finished ===========

            }

        }
    }


}