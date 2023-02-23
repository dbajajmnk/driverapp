package com.hbeonlabs.driversalerts.ui.fragment.camera

import android.Manifest
import android.app.Dialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentCameraBinding
import com.hbeonlabs.driversalerts.ui.base.BaseFragment
import com.hbeonlabs.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.hbeonlabs.driversalerts.utils.DrowsinessAnalyser
import com.hbeonlabs.driversalerts.utils.LiveStreamingHelper
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.CAMERA_PERMISSION_REQ_CODE
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(), EasyPermissions.PermissionCallbacks,
    ActivityCompat.OnRequestPermissionsResultCallback {
    private var cameraProvider: ProcessCameraProvider? = null
    var alarmTriggered = false
    lateinit var drowsinessAlertDialog: Dialog
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    val liveStreamingHelper = LiveStreamingHelper()
    override fun initView() {
        super.initView()
        drowsinessAlertDialog = dialogDrowsinessAlert()
        askCameraPermission()
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


    override fun onPause() {
        super.onPause()
        if (alarmTriggered) {
            drowsinessAlertDialog.dismiss()
        }
    }

    private fun doOnCameraPermissionGranted() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        binding.previewView.scaleType = PreviewView.ScaleType.FILL_CENTER

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            initializeCameraAndDoAnalysis()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun initializeCameraAndDoAnalysis() {
        val preview = Preview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            DrowsinessAnalyser({
                drowsinessAlertDialog.show()
            }, {
                drowsinessAlertDialog.dismiss()
            })
        )

        try {
            cameraProviderFuture.get().bindToLifecycle(
                viewLifecycleOwner,
                selector,
                preview,
                imageAnalysis
            )
            liveStreamingHelper.start()
        } catch (e: Exception) {
            e.printStackTrace()
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
}

