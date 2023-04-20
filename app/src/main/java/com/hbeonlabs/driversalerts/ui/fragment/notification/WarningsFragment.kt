package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentWarningsBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.utils.collectLatestLifeCycleFlow
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class WarningsFragment : BaseFragment<FragmentWarningsBinding>(){

    @Inject
    lateinit var itemAdapter: NotificationAdapter

    private val viewModel:WarningViewModel by viewModels()

    override fun initView() {
        super.initView()

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
        collectLatestLifeCycleFlow(viewModel.getWarningList())
        {
            Log.d("TAG", "observe: "+it)
            itemAdapter.differ.submitList(it)
        }
    }

}