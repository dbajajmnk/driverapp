package com.hbeonlabs.driversalerts.ui.fragment.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentAttendanceBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment

class AttendanceFragment : BaseFragment<FragmentAttendanceBinding>() {

    override fun initView() {
        super.initView()

    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_attendance
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false);



        return view
    }



}