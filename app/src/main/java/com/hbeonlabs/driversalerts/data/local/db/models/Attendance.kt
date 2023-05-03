package com.hbeonlabs.driversalerts.data.local.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id:Long? = null,
    val tagID:String,
    val inTime: String,
    val outTime:String,
    val day:String,
    val dayTime:Int
)
