package com.hbeonlabs.driversalerts.ui.fragment.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentLogsBinding
import com.hbeonlabs.driversalerts.databinding.FragmentNotificationBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import javax.inject.Inject

class LogsFragment : BaseFragment<FragmentLogsBinding>(){

    private lateinit var recyclerView: RecyclerView
    @Inject
     lateinit var itemAdapter: NotificationAdapter

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_logs
    }


    override fun initView() {
        super.initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_logs, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
       // itemAdapter = NotificationAdapter(getItems(), this)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
          //  adapter = itemAdapter
        }

        return view
    }


}