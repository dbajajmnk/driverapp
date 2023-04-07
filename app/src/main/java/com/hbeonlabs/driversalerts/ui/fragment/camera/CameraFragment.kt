package com.hbeonlabs.driversalerts.ui.fragment.camera

import android.Manifest
import android.app.Dialog
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.common.InputImage
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentCameraBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.DriverLocationProvider
import com.hbeonlabs.driversalerts.utils.DrowsinessDetector
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.CAMERA_PERMISSION_REQ_CODE
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.EglRenderer.FrameListener
import org.webrtc.SurfaceViewRenderer
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(), EasyPermissions.PermissionCallbacks,
    ActivityCompat.OnRequestPermissionsResultCallback {

    lateinit var drowsinessAlertDialog: Dialog
    private val webRtcHelper = WebRtcHelper.getInstance()
    private val drowsinessDetector = DrowsinessDetector({
        drowsinessAlertDialog.show()
    }, {
        drowsinessAlertDialog.dismiss()
    })
    private val frameListener = FrameListener {
        it?.let { drowsinessDetector.detectDrowsiness(InputImage.fromBitmap(it,0)) }
    }
    private lateinit var driverLocationProvider: DriverLocationProvider
    private val recorder = Recorder()

    override fun initView() {
        super.initView()
        drowsinessAlertDialog = dialogDrowsinessAlert()
        askCameraPermission()
        driverLocationProvider = DriverLocationProvider(requireActivity(),webRtcHelper)
        //recorder.init(binding.frontSurfaceview)
        //recorder.toggleRecording(true)
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
        webRtcHelper.init(requireContext())
        webRtcHelper.startFrontStreaming(binding.frontSurfaceview)
        webRtcHelper.startBackStreaming(binding.backSurfaceview)
        webRtcHelper.startVideoStreaming()
//        Timer().schedule(100, 100) {
//            webRtcHelper.addFrameListener(frameListener)
//        }
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


    override fun onDestroy() {
        super.onDestroy()
        webRtcHelper.onDestroy()
        driverLocationProvider.onDestroy()
    }
}

