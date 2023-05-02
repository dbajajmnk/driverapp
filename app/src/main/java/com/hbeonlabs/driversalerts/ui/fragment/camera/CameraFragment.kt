package com.hbeonlabs.driversalerts.ui.fragment.camera

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.mlkit.vision.common.InputImage
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.bluetooth.AttendanceCallback
import com.hbeonlabs.driversalerts.bluetooth.AttendanceManager
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.databinding.FragmentCameraBinding
import com.hbeonlabs.driversalerts.ui.activity.HomeActivity
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.DriverLocationProvider
import com.hbeonlabs.driversalerts.utils.DrowsinessDetector
import com.hbeonlabs.driversalerts.utils.collectLatestLifeCycleFlow
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.OVERSPEEDING_THRESHOLD
import com.hbeonlabs.driversalerts.utils.makeToast
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.EglRenderer.FrameListener
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>() {

    private val viewModel:CameraViewModel by viewModels()
    lateinit var locationProvider : DriverLocationProvider
    lateinit var overSpeedingAlertdialog : Dialog
    lateinit var drowsinessAlertDialog: Dialog
    private val webRtcHelper = WebRtcHelper.getInstance()
    private lateinit var currentLocationData :LocationAndSpeed
    lateinit var timer :Timer
    private var showDialogs = false
    private val drowsinessDetector = DrowsinessDetector({
        if(showDialogs) {
            createNotification(
                AppConstants.NotificationSubType.DROWSNISS.toString(),
                AppConstants.DROWSINESS_MESSAGE,
                AppConstants.NotificationType.WARNING.ordinal
            )
            drowsinessAlertDialog.show()
        }
    }, {
        if(showDialogs) {
            drowsinessAlertDialog.dismiss()
        }
    })
    private val frameListener = FrameListener {
        it?.let { drowsinessDetector.detectDrowsiness(InputImage.fromBitmap(it,0)) }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_camera
    }

    override fun initView() {
        super.initView()

        viewModel.getDeviceConfiguration()
        initDialogs()
        initLocationProvider()
        initWebRtc()
        initAttendanceManager()
        createNotification(AppConstants.NotificationSubType.STREAMING_START.toString(), AppConstants.STEAMING_START_MESSAGE, AppConstants.NotificationType.LOG.ordinal)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        createNotification(AppConstants.NotificationSubType.STREAMING_STOP.toString(), AppConstants.STEAMING_STOP_MESSAGE, AppConstants.NotificationType.LOG.ordinal)
    }
    override fun onResume() {
        super.onResume()
        showDialogs = true
    }

    override fun onPause() {
        super.onPause()
        showDialogs = false
    }
    private fun initAttendanceManager(){
        if (!EasyPermissions.hasPermissions(requireContext(), *HomeActivity.RequiredPermissions.bluetoothPermissions)) {
            Toast.makeText(requireContext(), "Please provide bluetooth permission to connect with RFID device", Toast.LENGTH_SHORT).show()
        } else {
            val callback = object : AttendanceCallback {
                override fun onAttendance(attendanceModel: AttendanceModel) {
                    viewModel.addAttendance(attendanceModel)
                }

                override fun onConnect(status: Boolean) {
                    val notificationType = if(status) AppConstants.NotificationType.LOG else AppConstants.NotificationType.WARNING
                    val msg = if(status) AppConstants.RFID_CONNECTION_SUCCESS_MESSAGE else AppConstants.RFID_CONNECTION_FAIL_MESSAGE
                    createNotification(AppConstants.NotificationSubType.RFID_CONNECTION.toString(), msg, notificationType.ordinal)
                }
            }
            val observer = AttendanceManager(requireActivity(), callback)
            lifecycle.addObserver(observer)
        }
    }

    private fun initWebRtc() {
        if (!EasyPermissions.hasPermissions(requireContext(), *HomeActivity.RequiredPermissions.cameraPermissions)) {
            Toast.makeText(requireContext(), "Please provide camera permission for live streaming", Toast.LENGTH_SHORT).show()
        } else {
            webRtcHelper.init(requireContext())
            webRtcHelper.startFrontStreaming(binding.frontSurfaceview)
            webRtcHelper.startBackStreaming(binding.backSurfaceview)
            timer = Timer()
            timer.schedule(100, 100) {
                webRtcHelper.addFrameListener(frameListener)
            }
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

    override fun onDestroy() {
        super.onDestroy()
        webRtcHelper.onDestroy()
        locationProvider.onDestroy()
        if(this@CameraFragment::timer.isInitialized)
            timer.cancel()
    }

    private fun initLocationProvider() {
        if(!EasyPermissions.hasPermissions(requireContext(), *HomeActivity.RequiredPermissions.locationPermissions)){
            Toast.makeText(requireContext(), "Please provide location permissions to get vehicle location.", Toast.LENGTH_SHORT).show()
        }else{
        locationProvider = DriverLocationProvider(this) { locationAndSpeedData ->
            currentLocationData = locationAndSpeedData
            val speedInKmph = "%.2f".format(locationAndSpeedData.speed.toDouble() * 3.6)
            binding.tvSpeedData.text = "Speed =  $speedInKmph"
            viewModel.addLocationData(locationAndSpeedData)
            if (speedInKmph.toFloat() >= OVERSPEEDING_THRESHOLD) {
                createNotification(AppConstants.NotificationSubType.OVERSPEEDING.toString(), AppConstants.OVERSPEEDING_MESSAGE, AppConstants.NotificationType.WARNING.ordinal)
                overSpeedingAlertdialog.show()
            } else {
                overSpeedingAlertdialog.dismiss()
            }
        }
        }
    }

    override fun observe() {
        super.observe()
        collectLatestLifeCycleFlow(viewModel.getLast5LocationData()) {
            if (it.isNotEmpty()) {
                val acceleration = it.first().speed.toFloat() - it.last().speed.toFloat()
                binding.tvAccelerationData.text = acceleration.toString()
                if (acceleration > 10) {
                    makeToast("Instant SpeedUp")
                } else if (acceleration < -15) {
                    makeToast("Immediate breaking")
                }
            }
        }
    }

    private fun getLat() : String{
        return if(this@CameraFragment::currentLocationData.isInitialized)
            return currentLocationData.locationLatitude
        else
            "1.0"
    }

    private fun getLong() : String{
        return if(this@CameraFragment::currentLocationData.isInitialized)
            return currentLocationData.locationLongitude
        else
            "1.0"
    }

    private fun createNotification(title: String, msg : String, notificationType: Int){
        viewModel.createNotification(
            Warning(
                timeInMills = ""+System.currentTimeMillis(),
                locationLatitude = getLat(),
                locationLongitude = getLong(),
                notificationTitle = title,
                message = msg,
                notificationType = notificationType,
                isSynced = false
            )
        )
    }
}

