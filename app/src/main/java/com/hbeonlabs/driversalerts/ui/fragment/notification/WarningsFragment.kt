package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentWarningsBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.utils.collectLatestLifeCycleFlow
import com.hbeonlabs.driversalerts.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WarningsFragment : BaseFragment<FragmentWarningsBinding>(){

    @Inject
    lateinit var itemAdapter: NotificationAdapter

    private val viewModel:WarningViewModel by viewModels()

    override fun initView() {
        super.initView()
        checkPagingStates()

        viewModel.getAllNotificationsFromApi()

        itemAdapter.setOnItemClickListener {
            findNavController().navigate(NotificationsFragmentDirections.actionNotificationsFragmentToNotificationDisplay(it))
        }

        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_warnings

    }

    override fun observe() {
        super.observe()
        collectLatestLifeCycleFlow(viewModel.getAllNotificationsFromApi())
        {
            itemAdapter.submitData(it)
        }

        collectLatestLifeCycleFlow(viewModel.getWarningList())
        {
            Log.d("TAG", "observe: "+it)
            //itemAdapter.differ.submitList(it)
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
                  //  itemAdapter.differ.submitList(it.notifications)
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