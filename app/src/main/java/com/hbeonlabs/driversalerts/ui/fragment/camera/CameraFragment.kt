package com.hbeonlabs.driversalerts.ui.fragment.camera

import android.Manifest
import android.app.Dialog
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import com.google.common.util.concurrent.ListenableFuture
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
    private var cameraProvider: ProcessCameraProvider? = null
    var isPaused = false
    lateinit var drowsinessAlertDialog: Dialog
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
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
        webRtcHelper.start(requireContext(), binding.senderSurfaceview,null)
        Timer().schedule(100, 100) {
            webRtcHelper.addFrameListener(frameListener)
        }
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
        isPaused = true
    }

    private fun doOnCameraPermissionGranted() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        /*binding.senderSurfaceview.addFrameListener({
            Log.v("Image","Got frame from webrtc $it")
            it?.let { drowsinessDetector.detectDrowsiness(InputImage.fromBitmap(it,0)) }
        },1.0f)*/
        //binding.previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
        /*cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            initializeCameraAndDoAnalysis()
        }, ContextCompat.getMainExecutor(requireContext()))*/
    }

    private fun initializeCameraAndDoAnalysis() {
        /*val preview = Preview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            DrowsinessDetector({
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

        } catch (e: Exception) {
            e.printStackTrace()
        }*/
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
    }
}

