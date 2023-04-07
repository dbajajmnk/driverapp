package com.hbeonlabs.driversalerts.ui.fragment.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentHistoryBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment

class HistoryTimeFragment : BaseFragment<FragmentHistoryBinding>(){


    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: HistoryTimeAdapter
    val itemDecoration = RecyclerViewItemDecoration(16,50)

    override fun initView() {
        super.initView()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_history_time
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_time, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_time)
        itemAdapter = HistoryTimeAdapter(getItems())
        recyclerView.addItemDecoration(itemDecoration)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        return view
    }

    private fun getItems(): List<ItemT> {
        return listOf(
            ItemT("Recording 1","6:30 am - 8:30 am"),
            ItemT("Recording 2", "6:30 am - 8:30 am"),
        )
    }
}