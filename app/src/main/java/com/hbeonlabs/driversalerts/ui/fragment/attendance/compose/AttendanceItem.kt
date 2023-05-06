package com.hbeonlabs.driversalerts.ui.fragment.attendance.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel

@Composable
fun AttendanceItem(isOddItem:Boolean, attendanceModel: AttendanceModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.96f)
            .wrapContentHeight()
            .then(
                if (isOddItem) {
                    Modifier.background(colorResource(id = R.color.lightblue))
                } else {
                    Modifier.background(colorResource(id = R.color.white))
                }
            )



    ) {
        AttendanceText(modifier = Modifier.weight(0.4f), text = attendanceModel.tagId)
        AttendanceText(modifier = Modifier.weight(0.4f),text = "Class")
        AttendanceText(modifier = Modifier.weight(0.4f),text = "In Time")
        AttendanceText(modifier = Modifier.weight(0.4f),text = "Out Time")
    }

}



@Composable
fun AttendanceText(modifier: Modifier= Modifier, text:String) {
    Text(
        modifier = modifier
            .padding(6.dp),
        text = text,
        color = colorResource(id = R.color.dark_color)
    )
}

