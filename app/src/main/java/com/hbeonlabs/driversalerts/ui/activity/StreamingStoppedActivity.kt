package com.hbeonlabs.driversalerts.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityMainBinding
import com.hbeonlabs.driversalerts.databinding.ActivityNoRecordingBinding
import com.hbeonlabs.driversalerts.ui.base.BaseActivity

class StreamingStoppedActivity : BaseActivity<ActivityNoRecordingBinding>() {

    override fun getLayoutResourceId(): Int = R.layout.activity_no_recording

    override fun initView() {
        super.initView()

    }

}