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

class LogsFragment : BaseFragment<FragmentLogsBinding>() , NotificationAdapter.OnItemClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: NotificationAdapter

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_logs
    }


    override fun initView() {
        super.initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_logs, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        itemAdapter = NotificationAdapter(getItems(), this)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        return view
    }

    private fun getItems(): List<Item> {
        return listOf(
            Item("Live streaming", R.drawable.red_alert,"Stop","Driver is delay due to traffic jam in location malviya nagar", "17-02-2-23 at 3:54 PM"),
            Item("Live streaming", R.drawable.red_alert,"Stop","Driver is delay due to traffic jam in location malviya nagar", "17-02-2-23 at 3:54 PM")
        )
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireContext(),"Item $position clicked", Toast.LENGTH_SHORT)
    }

}