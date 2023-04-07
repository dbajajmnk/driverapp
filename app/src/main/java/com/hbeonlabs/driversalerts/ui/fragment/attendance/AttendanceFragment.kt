package com.hbeonlabs.driversalerts.ui.fragment.attendance

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentAttendanceBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.history.HistoryTimeAdapter
import com.hbeonlabs.driversalerts.ui.fragment.history.ItemT
import com.hbeonlabs.driversalerts.ui.fragment.history.RecyclerViewItemDecoration

class AttendanceFragment : BaseFragment<FragmentAttendanceBinding>() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: AttendanceAdapter

    override fun initView() {
        super.initView()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_attendance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attendance, container, false);
        val titleView = view.findViewById<TextView>(R.id.title_frag)

        titleView.text = "Attendance"

        recyclerView = view.findViewById(R.id.recyclerview)
        itemAdapter = AttendanceAdapter(getItems())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = itemAdapter
        }

        return view
    }

    private fun getItems(): List<ItemAttendance> {
        return listOf(
            ItemAttendance("Karman Singh","11 A","20:35","20:50",Color.BLUE),
            ItemAttendance("Karman Singh","11 A","20:35","20:50",Color.WHITE),
        )
    }


}