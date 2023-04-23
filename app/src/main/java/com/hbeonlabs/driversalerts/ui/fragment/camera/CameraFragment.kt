package com.hbeonlabs.driversalerts.ui.fragment.camera

import android.Manifest
import android.app.Dialog
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import com.google.mlkit.vision.common.InputImage
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.bluetooth.AttendanceCallback
import com.hbeonlabs.driversalerts.bluetooth.AttendanceManager
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.databinding.FragmentCameraBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.DriverLocationProvider
import com.hbeonlabs.driversalerts.utils.DrowsinessDetector
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.CAMERA_PERMISSION_REQ_CODE
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.LOCATION_PERMISSION_REQ_CODE
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.OVERSPEEDING_THRESHOLD
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.EglRenderer.FrameListener
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(), EasyPermissions.PermissionCallbacks,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private val viewModel:CameraViewModel by viewModels()
    lateinit var locationProvider : DriverLocationProvider
    lateinit var overSpeedingAlertdialog : Dialog
    lateinit var drowsinessAlertDialog: Dialog
    private val webRtcHelper = WebRtcHelper.getInstance()
    private lateinit var currentLocationData :LocationAndSpeed
    lateinit var timer :Timer

    private var locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val drowsinessDetector = DrowsinessDetector({
        if(this@CameraFragment::currentLocationData.isInitialized) {
            viewModel.addWarningsData(
                Warning(
                    timeInMills = currentLocationData.timeInMills,
                    locationLatitude = currentLocationData.locationLatitude,
                    locationLongitude = currentLocationData.locationLongitude,
                    notificationTitle = AppConstants.NotificationSubType.DROWSNISS.toString(),
                    notificationType = AppConstants.NotificationType.WARNING.ordinal,
                    message = AppConstants.DROWSINESS_MESSAGE,
                    isSynced = false
                )
            )
        }
        drowsinessAlertDialog.show()
    }, {
        drowsinessAlertDialog.dismiss()
    })
    private val frameListener = FrameListener {
        it?.let { drowsinessDetector.detectDrowsiness(InputImage.fromBitmap(it,0)) }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_camera
    }


    override fun initView() {
        super.initView()
        initDialogs()
        askLocationPermissions()
        askCameraPermission()
        initAttendanceManager()
    }

    private fun initAttendanceManager(){
        val attendanceCallBack =
            AttendanceCallback { attendanceModel ->
                println(attendanceModel)
                viewModel.addAttendance(attendanceModel)
            }
        val observer = AttendanceManager(requireActivity(),attendanceCallBack)
        lifecycle.addObserver(observer)
    }

    private fun askCameraPermission() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)) {
            doOnCameraPermissionGranted()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This application compulsory needs camera permission to work.",
                CAMERA_PERMISSION_REQ_CODE,
                Manifest.permission.CAMERA
            )
        }
    }

    private fun askLocationPermissions()
    {
        if (EasyPermissions.hasPermissions(requireContext(), *locationPermissions)) {
            doOnLocationPermissionAvailable()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This application compulsory needs location permission to work.",
                LOCATION_PERMISSION_REQ_CODE,
                *locationPermissions
            )
        }

    }

    private fun doOnCameraPermissionGranted() {
        webRtcHelper.init(requireContext())
        webRtcHelper.startFrontStreaming(binding.frontSurfaceview)
        //webRtcHelper.start(requireContext(), binding.frontSurfaceview,null)
        //webRtcHelper.startBackStreaming(binding.backSurfaceview)
        timer = Timer()
        timer.schedule(100, 100) {
            webRtcHelper.addFrameListener(frameListener)
        }
    }

    private fun initDialogs()
    {
        drowsinessAlertDialog = dialogDrowsinessAlert(
            headerText = "Drowsy Alert!!!",
            descText = "Tracker suspects that the driver is experiencing Drowsiness. Touch OK Stop the Alarm"
        )
        overSpeedingAlertdialog = dialogDrowsinessAlert(
            headerText = "Over Speeding Alert!!!",
            descText = "Tracker suspects that the driver is Over Speeding. Please slow down!. Touch OK Stop the Alarm"
        )
    }




    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when(requestCode)
        {
            CAMERA_PERMISSION_REQ_CODE ->{
                askCameraPermission()
            }

            LOCATION_PERMISSION_REQ_CODE ->{
               askLocationPermissions()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        when(requestCode)
        {
            CAMERA_PERMISSION_REQ_CODE ->{
                if (EasyPermissions.somePermissionDenied(this, perms.first())) {
                    AppSettingsDialog.Builder(requireActivity()).build().show()
                } else {
                    askCameraPermission()
                }
            }

            LOCATION_PERMISSION_REQ_CODE ->{
                if (EasyPermissions.somePermissionDenied(this, perms.first())) {
                    AppSettingsDialog.Builder(requireActivity()).build().show()
                } else {
                    askLocationPermissions()
                }
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        webRtcHelper.onDestroy()
        locationProvider.onDestroy()
        if(this@CameraFragment::timer.isInitialized)
            timer.cancel()
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

    private fun doOnLocationPermissionAvailable() {
        locationProvider = DriverLocationProvider(this){locationAndSpeedData->
            binding.tvSpeedData.text = "Speed =  ${locationAndSpeedData.speed}"
            viewModel.addLocationData(locationAndSpeedData)
            currentLocationData = locationAndSpeedData
            if (locationAndSpeedData.speed.toFloat() >= OVERSPEEDING_THRESHOLD)
            {
                viewModel.addWarningsData(Warning(
                    timeInMills = locationAndSpeedData.timeInMills,
                    locationLatitude = locationAndSpeedData.locationLatitude,
                    locationLongitude = locationAndSpeedData.locationLongitude,
                    notificationTitle = AppConstants.NotificationSubType.OVERSPEEDING.toString(),
                    message = AppConstants.OVERSPEEDING_MESSAGE,
                    notificationType = AppConstants.NotificationType.WARNING.ordinal,
                    isSynced = false
                ))
                overSpeedingAlertdialog.show()
            }
            else
            {
                overSpeedingAlertdialog.dismiss()
            }
        }
    }
    }

