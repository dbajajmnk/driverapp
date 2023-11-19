package com.hbeonlabs.driversalerts.ui.fragment.dashboard

import android.Manifest
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.bluetooth.AttendanceCallback
import com.hbeonlabs.driversalerts.bluetooth.AttendanceManager
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.databinding.FragmentDashboardBinding
import com.hbeonlabs.driversalerts.ui.activity.HomeActivity
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.streaming.StreamingHelper
import com.hbeonlabs.driversalerts.utils.DriverLocationProvider
import com.hbeonlabs.driversalerts.utils.DrowsinessDetector
import com.hbeonlabs.driversalerts.utils.Utils
import com.hbeonlabs.driversalerts.utils.batteryChargingStatusChecker
import com.hbeonlabs.driversalerts.utils.collectLatestLifeCycleFlow
import com.hbeonlabs.driversalerts.utils.constants.AppConstants
import com.hbeonlabs.driversalerts.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.webrtc.EglRenderer
import pub.devrel.easypermissions.EasyPermissions
import java.util.Timer
import kotlin.concurrent.schedule

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>() {
    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_dashboard
    }
    private val viewModel: DashboardViewModel by viewModels()
    lateinit var locationProvider: DriverLocationProvider
    lateinit var overSpeedingAlertdialog: Dialog
    lateinit var drowsinessAlertDialog: Dialog
    private lateinit var currentLocationData: LocationAndSpeed
    lateinit var timer: Timer
    private var showDialogs = false
    private val drowsinessDetector = DrowsinessDetector({
        if (showDialogs) {
            createNotification(
                AppConstants.NotificationSubType.DROWSNISS.toString(),
                AppConstants.DROWSINESS_MESSAGE,
                AppConstants.NotificationType.WARNING.ordinal
            )
            drowsinessAlertDialog.show()
        }
    }, {
        if (showDialogs) {
            drowsinessAlertDialog.dismiss()
        }
    })
    private val frameListener = EglRenderer.FrameListener {
        it?.let { drowsinessDetector.detectDrowsiness(InputImage.fromBitmap(it, 0)) }
    }
    lateinit var streamingHelper: StreamingHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDeviceConfiguration()
        initDialogs()
        initLocationProvider()
        initStreamingHelper()
        initAttendanceManager()
        createNotification(
            AppConstants.NotificationSubType.STREAMING_START.toString(),
            AppConstants.STEAMING_START_MESSAGE,
            AppConstants.NotificationType.LOG.ordinal
        )
        observe()
    }

    override fun onResume() {
        super.onResume()
        showDialogs = true
    }

    override fun onPause() {
        super.onPause()
        showDialogs = false
    }

    private fun initAttendanceManager() {
        if (!EasyPermissions.hasPermissions(
                requireContext(),
                *arrayOf(Manifest.permission.BLUETOOTH_CONNECT)
            )
        ) {
            makeToast("Please provide bluetooth permission to connect with RFID device")
        } else {
            val callback = object : AttendanceCallback {
                override fun onAttendance(attendanceModel: AttendanceModel) {
                    viewModel.addAttendance(attendanceModel)
                }

                override fun onConnect(status: Boolean) {
                    val notificationType =
                        if (status) AppConstants.NotificationType.LOG else AppConstants.NotificationType.WARNING
                    val msg =
                        if (status) AppConstants.RFID_CONNECTION_SUCCESS_MESSAGE else AppConstants.RFID_CONNECTION_FAIL_MESSAGE
                    createNotification(
                        AppConstants.NotificationSubType.RFID_CONNECTION.toString(),
                        msg,
                        notificationType.ordinal
                    )
                }
            }
            val observer = AttendanceManager(requireActivity(), callback)
            lifecycle.addObserver(observer)
        }
    }

    private fun initStreamingHelper() {
        if (!EasyPermissions.hasPermissions(
                requireContext(),
                *arrayOf(Manifest.permission.CAMERA)
            )
        ) {
            makeToast("Please provide camera permission for live streaming")
        } else {
            streamingHelper = StreamingHelper(requireActivity(), viewModel, binding.frontRenderer, binding.backRenderer)
            streamingHelper.startStreaming(lifecycleScope,viewLifecycleOwner)
            timer = Timer()
            timer.schedule(100, 100) {
                //binding.frontRenderer.addFrameListener(frameListener,1.0f)
                updateTimeTexts()
                //checkChargingStatus()
            }
        }
    }

    private fun updateTimeTexts() {
        requireActivity().runOnUiThread {
            if(streamingHelper.getFrontStreamingStatus().equals("Started"))
                binding.frontText.text = "Front\n${Utils.getCurrentTimeString()}"
            else
                binding.frontText.text = streamingHelper.getFrontStreamingStatus()
            if(streamingHelper.getBackStreamingStatus().equals("Started"))
                binding.backText.text = "Back\n${Utils.getCurrentTimeString()}"
            else
                binding.backText.text = streamingHelper.getBackStreamingStatus()

        }
    }

    private fun checkChargingStatus(){
        val charging = activity?.batteryChargingStatusChecker()
        if(charging == false){
            activity?.finish()
        }
    }

    private fun initDialogs() {
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
        if (this@DashboardFragment::streamingHelper.isInitialized) streamingHelper.disconnect()
        if (this@DashboardFragment::locationProvider.isInitialized) locationProvider.onDestroy()
        if (this@DashboardFragment::timer.isInitialized) timer.cancel()
        super.onDestroy()
    }

    private fun initLocationProvider() {
        if (!EasyPermissions.hasPermissions(
                requireContext(),
                *arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,)
            )
        ) {
            makeToast("Please provide location permissions to get vehicle location.")
        } else {
            locationProvider = DriverLocationProvider(this) { locationAndSpeedData ->
                currentLocationData = locationAndSpeedData
                //lifecycleScope.launch {streamingHelper.sendLocation(currentLocationData)}  TODO enable it
                val speedInKmph = "%.2f".format(locationAndSpeedData.speed.toDouble() * 3.6)
                binding.tvSpeedData.text = "Speed =  $speedInKmph"
                viewModel.addLocationData(locationAndSpeedData)
                if (speedInKmph.toFloat() >= AppConstants.OVERSPEEDING_THRESHOLD) {
                    createNotification(
                        AppConstants.NotificationSubType.OVERSPEEDING.toString(),
                        AppConstants.OVERSPEEDING_MESSAGE,
                        AppConstants.NotificationType.WARNING.ordinal
                    )
                    overSpeedingAlertdialog.show()
                } else {
                    overSpeedingAlertdialog.dismiss()
                }
            }
        }
    }

    override fun observe() {
        collectLatestLifeCycleFlow(viewModel.getLast5LocationData()) {
            if (it.isNotEmpty()) {
                val acceleration = it.first().speed.toFloat() - it.last().speed.toFloat()
                //binding.tvAccelerationData.text = acceleration.toString()
                if (acceleration > 10) {
                    makeToast("Instant SpeedUp")
                } else if (acceleration < -15) {
                    makeToast("Immediate breaking")
                }
            }
        }
    }

    private fun getLat(): String {
        return if (this@DashboardFragment::currentLocationData.isInitialized)
            return currentLocationData.locationLatitude
        else
            "1.0"
    }

    private fun getLong(): String {
        return if (this@DashboardFragment::currentLocationData.isInitialized)
            return currentLocationData.locationLongitude
        else
            "1.0"
    }

    private fun createNotification(title: String, msg: String, notificationType: Int) {
        /*viewModel.createNotification(
            Warning(
                timeInMills = "" + System.currentTimeMillis(),
                locationLatitude = getLat(),
                locationLongitude = getLong(),
                notificationTitle = title,
                message = msg,
                notificationType = notificationType,
                isSynced = false
            )
        )*/
    }
}