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
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.data.local.db.LocationAndSpeedDao
import com.hbeonlabs.driversalerts.databinding.FragmentHomeBinding
import com.hbeonlabs.driversalerts.receivers.DailyAlarmReceiver
import com.hbeonlabs.driversalerts.receivers.EndAlarmReceiver
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.batteryChargingStatusChecker
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.END_HOUR
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.END_MINUTES
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.START_HOUR
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.START_MINUTES
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
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
    }
    @Inject
    lateinit var speedDao: LocationAndSpeedDao

    lateinit var binding: FragmentHomeBinding
    lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog = dialogDrowsinessAlert(headerText = "Alert!", "You have crossed the Speed Limit")
        //askLocationPermission()

        binding = DataBindingUtil.setContentView(this, R.layout.fragment_home)
        binding.btnDriver.setOnClickListener(this)
        binding.btnAdmin.setOnClickListener(this)
        lifecycleScope.launchWhenStarted {
            while (true) {
                delay(3000)
                Log.d("TAG", "initView: " + batteryChargingStatusChecker())
            }
        }

        setADailyStartAndStopAlarm()
        askForPermissions()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_driver -> {
                startActivity(Intent(this, MainActivity::class.java).putExtra("from" , "HomePage"))
            }

            R.id.btn_admin -> {
                startActivity(Intent(this, AdminActivity::class.java))
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
                *RequiredPermissions.cameraPermissions
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
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when(requestCode)
        {
            AppConstants.CAMERA_PERMISSION_REQ_CODE ->{
                askCameraPermission()
            }

            AppConstants.LOCATION_PERMISSION_REQ_CODE ->{
                askLocationPermission()
            }

            AppConstants.BLUETOOTH_PERMISSION_REQ_CODE ->{
                askBluetoothPermission()
            }
        }
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

}