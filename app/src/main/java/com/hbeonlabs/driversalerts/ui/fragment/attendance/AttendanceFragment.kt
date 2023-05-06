package com.hbeonlabs.driversalerts.ui.fragment.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.databinding.FragmentAttendanceBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.attendance.compose.AttendanceHeaderItem
import com.hbeonlabs.driversalerts.ui.fragment.attendance.compose.AttendanceItem
import java.nio.file.WatchEvent

class AttendanceFragment : BaseFragment<FragmentAttendanceBinding>() {
    val list :ArrayList<AttendanceModel> = arrayListOf()

    override fun initView() {
        super.initView()

        binding.toolbar.titleFrag.text = "Attendance"

        binding.composeAttendanceView.apply {

            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                // In Compose world
                MaterialTheme {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AttendanceHeaderItem()
                        LazyColumn(modifier = Modifier
                            .clip(
                            RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 4.dp,
                            bottomEnd = 4.dp,
                            bottomStart = 4.dp,
                            ))
                            .shadow(elevation = 2.dp)
                        )
                        {
                            itemsIndexed(list){ index, item ->
                                AttendanceItem(
                                    isOddItem = index%2==0,
                                    attendanceModel = item
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_attendance
    }




}