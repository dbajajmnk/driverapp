package com.hbeonlabs.driversalerts.data.local.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey(autoGenerate = false)
    val timeInMills:String,
    val locationLatitude :String,
    val locationLongitude:String,
    val notificationSubType: Int,
    val notificationType: Int,
    val message:String,
    var isSynced :Boolean
):Serializable