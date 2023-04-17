package com.hbeonlabs.driversalerts.utils

import android.app.Activity
import android.app.NotificationManager
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.core.content.ContextCompat

fun Activity.batteryChargingStatusChecker():Boolean
{
    val bm = getSystemService(BATTERY_SERVICE) as BatteryManager
    ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
    val intent = this.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
    val chargingStatus = when (plugged) {
        BatteryManager.BATTERY_PLUGGED_AC -> {
            true
        }
        BatteryManager.BATTERY_PLUGGED_USB -> {
            true
        }
        BatteryManager.BATTERY_PLUGGED_WIRELESS -> {
            true
        }
        else -> {
            false
        }
    }
    bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    return chargingStatus
}