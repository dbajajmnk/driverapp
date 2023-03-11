package com.hbeonlabs.driversalerts.ui.fragment.camera

import android.Manifest
import android.app.Dialog
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.mlkit.vision.common.InputImage
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentCameraBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.DrowsinessDetector
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.CAMERA_PERMISSION_REQ_CODE
import com.shivam.androidwebrtc.tutorial.WebRtcHelper
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.EglRenderer.FrameListener
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(), EasyPermissions.PermissionCallbacks,
    ActivityCompat.OnRequestPermissionsResultCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest= LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //TODO send this location in socket
        }
    }

    lateinit var drowsinessAlertDialog: Dialog
    private val webRtcHelper = WebRtcHelper()
    private val drowsinessDetector = DrowsinessDetector({
        drowsinessAlertDialog.show()
    }, {
        drowsinessAlertDialog.dismiss()
    })
    private val frameListener = FrameListener {
        it?.let { drowsinessDetector.detectDrowsiness(InputImage.fromBitmap(it,0)) }
    }
    override fun initView() {
        super.initView()
        drowsinessAlertDialog = dialogDrowsinessAlert()
        askCameraPermission()
        startLocationUpdate()
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

    private fun doOnCameraPermissionGranted() {
        webRtcHelper.start(requireContext(), binding.senderSurfaceview,null)
        Timer().schedule(100, 100) {
            webRtcHelper.addFrameListener(frameListener)
        }
    }


    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_camera
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        askCameraPermission()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionDenied(this, perms.first())) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            askCameraPermission()
        }
    }


    /**
     * Request location update
     */
    private fun startLocationUpdate() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback,Looper.getMainLooper())
        } catch (e: SecurityException) {
            // lacking permission to access location
        }
    }
    /**
     * Stop location updates
     */
    private fun stopLocationsUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationsUpdate()
    }
}

