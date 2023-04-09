package com.hbeonlabs.driversalerts.data.local.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hbeonlabs.driversalerts.ui.fragment.notification.NotificationSubType

@Entity
data class Warning(
    @PrimaryKey(autoGenerate = false)
    val timeInMills:String,
    val locationLatitude :String,
    val locationLongitude:String,
    val notificationSubType: Int,
    val message:String
)