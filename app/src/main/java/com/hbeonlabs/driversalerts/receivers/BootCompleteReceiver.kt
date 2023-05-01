package com.hbeonlabs.driversalerts.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //TODO add log for device reboot
        Log.d("TAG", "BootCompleteReceiver onReceive")
    }
}