package com.hbeonlabs.driversalerts.ui.activity

import android.Manifest
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.data.local.db.LocationAndSpeedDao
import com.hbeonlabs.driversalerts.databinding.ActivityNoRecordingBinding
import com.hbeonlabs.driversalerts.receivers.DailyAlarmReceiver
import com.hbeonlabs.driversalerts.receivers.EndAlarmReceiver
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.END_HOUR
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.END_MINUTES
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.START_HOUR
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.START_MINUTES
import com.hbeonlabs.driversalerts.workManager.ChargingOnWorkManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity :
    AppCompatActivity(),
    View.OnClickListener,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    object RequiredPermissions {
        val cameraPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            )
        val wakeLockPermissions = arrayOf(Manifest.permission.WAKE_LOCK)
    }

    @Inject
    lateinit var speedDao: LocationAndSpeedDao

    lateinit var binding: ActivityNoRecordingBinding
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = dialogDrowsinessAlert(headerText = "Alert!", "You have crossed the Speed Limit")
        // askLocationPermission()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_no_recording)
        binding.buttonMoveToDashboard.setOnClickListener(this)
        //TODO commented just for development time, uncomment once dev done
        /*lifecycleScope.launchWhenStarted {
            var checkingBatteryChargingStatus = true
            while (checkingBatteryChargingStatus) {
                delay(3000)
                val status = batteryChargingStatusChecker()
                if(status){
                    checkingBatteryChargingStatus = false
                    startActivity(Intent(this@HomeActivity, MainActivity::class.java).putExtra("from" , "HomePage"))
                }
            }
        }*/
        managePermissions = ManagePermissions(
            this,
            RequiredPermissions.cameraPermissions.toList(),
            PermissionsRequestCode,
        )
        //beginWorkManager()
        managePermissions.checkPermissions()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_move_to_dashboard -> {
                startActivity(Intent(this, MainActivity::class.java).putExtra("from", "HomePage"))
            }
        }
    }

    private fun setADailyStartAndStopAlarm() {
        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val startIntent = Intent(applicationContext, DailyAlarmReceiver::class.java)
        val startPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                applicationContext,
                11111,
                startIntent,
                PendingIntent.FLAG_MUTABLE,
            )
        } else {
            PendingIntent.getBroadcast(
                applicationContext,
                11111,
                startIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val startCalender = Calendar.getInstance()
        startCalender.set(Calendar.HOUR_OF_DAY, START_HOUR)
        startCalender.set(Calendar.MINUTE, START_MINUTES)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startCalender.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            startPendingIntent,
        )

        val stopIntent = Intent(applicationContext, EndAlarmReceiver::class.java)
        val stopPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                applicationContext,
                1212121,
                stopIntent,
                PendingIntent.FLAG_MUTABLE,
            )
        } else {
            PendingIntent.getBroadcast(
                applicationContext,
                1212121,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE,
            )
        }
        val calander = Calendar.getInstance()
        calander.set(Calendar.HOUR_OF_DAY, END_HOUR)
        calander.set(Calendar.MINUTE, END_MINUTES)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calander.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            stopPendingIntent,
        )
    }
    // /////////////Charging code////////////////////

    private val worker = OneTimeWorkRequestBuilder<ChargingOnWorkManager>().setConstraints(
        Constraints.Builder()
            .setRequiresCharging(true)
            .build(),
    ).build()

    private val workManager = WorkManager.getInstance(this)

    private fun beginWorkManager() {
        workManager.beginUniqueWork(
            "test",
            ExistingWorkPolicy.REPLACE,
            worker,
        ).enqueue()
    }
}
