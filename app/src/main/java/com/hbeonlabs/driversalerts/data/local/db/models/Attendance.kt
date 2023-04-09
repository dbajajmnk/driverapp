package com.hbeonlabs.driversalerts.data.local.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hbeonlabs.driversalerts.ui.fragment.notification.NotificationSubType

@Entity
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id:Long? = null,
    val name :String,
    val className:String,
    val inTime: String,
    val outTime:String,
    val day:String
)
