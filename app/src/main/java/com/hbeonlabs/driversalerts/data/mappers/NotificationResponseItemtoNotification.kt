package com.hbeonlabs.driversalerts.data.mappers

import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.remote.response.NotificationResponseItem
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun NotificationResponseItem.toNotification(): Warning
{
    val notificationType =  when (this.type)
    {
         "log" -> AppConstants.NotificationType.LOG.ordinal
         "warning" -> AppConstants.NotificationType.WARNING.ordinal
         else -> AppConstants.NotificationType.WARNING.ordinal
    }

    val dateTimeStr = "${this.date} ${this.time}"
    val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)
    val date = sdf.parse(dateTimeStr)
    val calendar = Calendar.getInstance()
    calendar.time = date


    val warning =  Warning(
        timeInMills =  calendar.timeInMillis.toString(),
        locationLatitude = this.latitude.toString(),
        locationLongitude = this.longitude.toString(),
        message = this.description.toString(),
        isSynced = true,
        notificationTitle = this.title.toString(),
        notificationType = notificationType
    )
    return warning

}