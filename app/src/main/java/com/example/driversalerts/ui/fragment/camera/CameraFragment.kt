package com.example.driversalerts.ui.fragment.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.example.driversalerts.R
import com.example.driversalerts.data.local.persistance.PrefManager
import com.example.driversalerts.databinding.FragmentCameraBinding
import com.example.driversalerts.ui.base.BaseFragment
import com.example.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.example.driversalerts.utils.DrowsinessAnalyser
import com.example.driversalerts.utils.constants.AppConstants.CAMERA_PERMISSION_REQ_CODE
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(),EasyPermissions.PermissionCallbacks,
    ActivityCompat.OnRequestPermissionsResultCallback {
    private var cameraProvider: ProcessCameraProvider? = null
    var alarmTriggered = false
    private val tag = "DriverAlert"
    val viewModel: CameraViewModel by viewModels()
    lateinit var drowsinessAlertDialog: Dialog
    private val TAG = "CameraXViewModel"
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val REQUIRED_PERMISSIONS =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    override fun initView() {
        super.initView()
        drowsinessAlertDialog = dialogDrowsinessAlert()
       askCameraPermission()
    }

    private fun askCameraPermission()
    {
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

        cameraProviderFuture.addListener(Runnable {
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
       if (EasyPermissions.somePermissionDenied(this,perms.first())){
           AppSettingsDialog.Builder(requireActivity()).build().show()
       }
        else{
            askCameraPermission()
       }
    }
}

