package com.hbeonlabs.driversalerts.data.local.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class LocationAndSpeed(
    @PrimaryKey(autoGenerate = false)
    val timeInMills:String,
    val locationLatitude :String,
    val locationLongitude:String,
    val speed:String

):Serializable
