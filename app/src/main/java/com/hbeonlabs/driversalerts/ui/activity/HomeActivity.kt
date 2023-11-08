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
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
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
import com.hbeonlabs.driversalerts.utils.batteryChargingStatusChecker
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.END_HOUR
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.END_MINUTES
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.START_HOUR
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.START_MINUTES
import com.hbeonlabs.driversalerts.workManager.ChargingOnWorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), View.OnClickListener, EasyPermissions.PermissionCallbacks,
    ActivityCompat.OnRequestPermissionsResultCallback {

    object RequiredPermissions{
        val cameraPermissions = arrayOf(
            Manifest.permission.CAMERA
        )
        val bluetoothPermissions = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT
        )
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
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
        //askLocationPermission()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_no_recording)
        binding.buttonMoveToDashboard.setOnClickListener(this)
        lifecycleScope.launchWhenStarted {
            var checkingBatteryChargingStatus = true
            while (checkingBatteryChargingStatus) {
                delay(3000)
                val status = batteryChargingStatusChecker()
                if(status){
                    checkingBatteryChargingStatus = false
                    startActivity(Intent(this@HomeActivity, MainActivity::class.java).putExtra("from" , "HomePage"))
                }
            }
        }

        //beginWorkManager()
        askForPermissions()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_move_to_dashboard -> {
                startActivity(Intent(this, MainActivity::class.java).putExtra("from" , "HomePage"))
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
                11111, startIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                applicationContext,
                11111, startIntent, PendingIntent.FLAG_IMMUTABLE
            )
        }


        val startCalender = Calendar.getInstance()
        startCalender.set(Calendar.HOUR_OF_DAY, START_HOUR)
        startCalender.set(Calendar.MINUTE, START_MINUTES)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startCalender.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            startPendingIntent
        )


        val stopIntent = Intent(applicationContext, EndAlarmReceiver::class.java)
        val stopPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                applicationContext,
                1212121, stopIntent, PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                applicationContext,
                1212121, stopIntent, PendingIntent.FLAG_IMMUTABLE
            )
        }
        val calander = Calendar.getInstance()
        calander.set(Calendar.HOUR_OF_DAY, END_HOUR)
        calander.set(Calendar.MINUTE, END_MINUTES)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calander.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            stopPendingIntent
        )

    }

    private fun askForPermissions()
    {
        askCameraPermission()
        askBluetoothPermission()
        askLocationPermission()
        askWakeLockPermission()
    }

    private fun askCameraPermission(){
        if (EasyPermissions.hasPermissions(this,*RequiredPermissions.cameraPermissions)) {
            Log.v("EasyPermissions", "Camera permissions granted.")
        } else {
            Log.v("EasyPermissions", "Camera permissions not granted.")
            EasyPermissions.requestPermissions(
                this,
                "Please provide Camera permission.",
                AppConstants.CAMERA_PERMISSION_REQ_CODE,
                *RequiredPermissions.cameraPermissions,
                *RequiredPermissions.bluetoothPermissions,
                *RequiredPermissions.locationPermissions,
                *RequiredPermissions.wakeLockPermissions
            )
        }
    }

    private fun askBluetoothPermission(){
        if (EasyPermissions.hasPermissions(this, *RequiredPermissions.bluetoothPermissions)) {
            Log.v("EasyPermissions", "Bluetooth permissions granted.")
        } else {
            Log.v("EasyPermissions", "Bluetooth permissions not granted.")
            EasyPermissions.requestPermissions(
                this,
                "Please provide Bluetooth permission.",
                AppConstants.BLUETOOTH_PERMISSION_REQ_CODE,
                *RequiredPermissions.bluetoothPermissions
            )
        }
    }

    private fun askLocationPermission(){
        if (EasyPermissions.hasPermissions(this, *RequiredPermissions.locationPermissions)) {
            Log.v("EasyPermissions", "Location permissions granted.")
        } else {
            Log.v("EasyPermissions", "Location permissions not granted.")
            EasyPermissions.requestPermissions(
                this,
                "Please provide Location permission.",
                AppConstants.LOCATION_PERMISSION_REQ_CODE,
                *RequiredPermissions.locationPermissions
            )
        }
    }

    private fun askWakeLockPermission(){
        if (EasyPermissions.hasPermissions(this, *RequiredPermissions.wakeLockPermissions)) {
            Log.v("EasyPermissions", "Bluetooth permissions granted.")
        } else {
            Log.v("EasyPermissions", "Bluetooth permissions not granted.")
            EasyPermissions.requestPermissions(
                this,
                "Please provide Bluetooth permission.",
                AppConstants.WAKE_LOCK_PERMISSION_REQ_CODE,
                *RequiredPermissions.wakeLockPermissions
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        when(requestCode)
        {
            AppConstants.CAMERA_PERMISSION_REQ_CODE ->{
                if (EasyPermissions.somePermissionDenied(this, perms.first())) {
                    AppSettingsDialog.Builder(this).build().show()
                } else {
                    askCameraPermission()
                }
            }

            AppConstants.LOCATION_PERMISSION_REQ_CODE ->{
                if (EasyPermissions.somePermissionDenied(this, perms.first())) {
                    AppSettingsDialog.Builder(this).build().show()
                } else {
                    askLocationPermission()
                }
            }

            AppConstants.BLUETOOTH_PERMISSION_REQ_CODE ->{
                if (EasyPermissions.somePermissionDenied(this, perms.first())) {
                    AppSettingsDialog.Builder(this).build().show()
                } else {
                    askBluetoothPermission()
                }
            }

            AppConstants.WAKE_LOCK_PERMISSION_REQ_CODE ->{
                if (EasyPermissions.somePermissionDenied(this, perms.first())) {
                    AppSettingsDialog.Builder(this).build().show()
                } else {
                    askWakeLockPermission()
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TAG", "onRequestPermissionsResult: "+permissions)

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    ///////////////Charging code////////////////////

    private val worker = OneTimeWorkRequestBuilder<ChargingOnWorkManager>().setConstraints(
        Constraints.Builder()
            .setRequiresCharging(true)
            .build()
    ).build()

    private val workManager = WorkManager.getInstance(this)

    fun beginWorkManager() {
        workManager.beginUniqueWork(
            "test",
            ExistingWorkPolicy.REPLACE,
            worker
        ).enqueue()
    }

}