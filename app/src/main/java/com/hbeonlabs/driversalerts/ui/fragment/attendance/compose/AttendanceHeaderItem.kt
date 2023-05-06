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

@Composable
fun AttendanceHeaderItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth(0.96f)
            .wrapContentHeight()
            .clip(RoundedCornerShape(
                topStart = 4.dp,
                topEnd = 4.dp
            ))
            .background(colorResource(id = R.color.darkblue))

    ) {
        HeaderText(modifier = Modifier.weight(0.4f), text = "Name")
        HeaderText(modifier = Modifier.weight(0.4f),text = "Class")
        HeaderText(modifier = Modifier.weight(0.4f),text = "In Time")
        HeaderText(modifier = Modifier.weight(0.4f),text = "Out Time")
    }

}

@Preview
@Composable
fun AttendanceHeaderItemPreview() {
    AttendanceHeaderItem()

}

@Composable
fun HeaderText(modifier: Modifier= Modifier, text:String) {
    Text(
        modifier = modifier
            .padding(6.dp),
        text = text,
        color = Color.White
    )
}

