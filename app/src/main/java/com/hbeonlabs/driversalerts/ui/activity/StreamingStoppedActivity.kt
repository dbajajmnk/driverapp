package com.hbeonlabs.driversalerts.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityMainBinding
import com.hbeonlabs.driversalerts.databinding.ActivityNoRecordingBinding
import com.hbeonlabs.driversalerts.ui.base.BaseActivity
import com.hbeonlabs.driversalerts.workManager.ChargingOnWorkManager

class StreamingStoppedActivity : BaseActivity<ActivityNoRecordingBinding>() {

    private val worker = OneTimeWorkRequestBuilder<ChargingOnWorkManager>().setConstraints(
        Constraints.Builder()
            .setRequiresCharging(true)
            .build()
    ).build()

    private val workManager = WorkManager.getInstance(this)

    override fun getLayoutResourceId(): Int = R.layout.activity_no_recording

    override fun initView() {
        super.initView()

        workManager.beginUniqueWork(
            "test",
            ExistingWorkPolicy.REPLACE,
            worker
        ).enqueue()
    }

}