package com.hbeonlabs.driversalerts.data.local.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationAndSpeed(
    @PrimaryKey(autoGenerate = false)
    val timeInMills:String,
    val locationLatitude :String,
    val locationLongitude:String,
    val speed:String,

)
