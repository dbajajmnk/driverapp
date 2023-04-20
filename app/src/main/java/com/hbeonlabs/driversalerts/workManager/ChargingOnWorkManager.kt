package com.hbeonlabs.driversalerts.workManager

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.hbeonlabs.driversalerts.ui.activity.MainActivity

class ChargingOnWorkManager(
    context: Context,
    params: WorkerParameters
):CoroutineWorker(context,params) {
    override suspend fun doWork(): Result {
        val intent = Intent(applicationContext,MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("from" , "WorkManager")
        applicationContext.startActivity(intent)
        Log.d("TAG", "doWork: charging ON")
      return  Result.success(workDataOf("Message" to "Completed"))
    }
}