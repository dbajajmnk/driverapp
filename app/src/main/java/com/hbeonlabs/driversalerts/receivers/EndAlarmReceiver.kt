package com.hbeonlabs.driversalerts.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.hbeonlabs.driversalerts.ui.activity.HomeActivity

class EndAlarmReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG", "onReceive: ")
        context?.startActivity(Intent(context, HomeActivity::class.java))
    }
}