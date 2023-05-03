package com.hbeonlabs.driversalerts.data.local.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id:Long? = null,
    val tagID:String,
    val inTime: String,
    val outTime:String  = "",
    val date:String,
    val dayTime:Int,
    val isSync :Boolean = false
)
