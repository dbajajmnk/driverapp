package com.example.driversalerts.ui.fragment.camera

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.driversalerts.R
import com.example.driversalerts.data.local.persistance.PrefManager
import com.example.driversalerts.databinding.FragmentCameraBinding
import com.example.driversalerts.ui.base.BaseFragment
import com.example.driversalerts.ui.fragment.dialogs.dialogDrowsinessAlert
import com.example.driversalerts.utils.DrowsinessAnalyser
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutionException

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(),
    ActivityCompat.OnRequestPermissionsResultCallback {
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var cameraSelector: CameraSelector? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var faceDetector: FaceDetector
    private var drowsyTime = 0L
    private val permissionRequestCode = 1001
    private lateinit var prefManager: PrefManager
    private var drowsyThreshold = 500
    private var cameraSelected = 0
    var drowsinessDetected = false
    var alarmTriggered = false
    private val tag = "DriverAlert"
    val viewModel: CameraViewModel by viewModels()
    lateinit var drowsinessAlertDialog :Dialog
    private val TAG = "CameraXViewModel"
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null

    private val REQUIRED_PERMISSIONS =
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

    override fun initView() {
        super.initView()
        drowsinessAlertDialog = dialogDrowsinessAlert()
        prefManager = PrefManager(requireContext())
        if (!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
        } else initViews()
    }

    override fun onResume() {
        super.onResume()
        Log.v(tag, "onResume alarmTriggered $alarmTriggered")
        drowsyThreshold = prefManager.getDuration()
        if (prefManager.getCameraSelected() != cameraSelected) {
            cameraSelected = prefManager.getCameraSelected()
            initViews()
        }
        if (alarmTriggered) {
           // playSound()
            drowsinessAlertDialog.show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (alarmTriggered) {
            drowsinessAlertDialog.dismiss()
        }
    }

    private fun initViews() {
        val preview = Preview.Builder().build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(requireContext()),
            DrowsinessAnalyser({
                drowsinessAlertDialog.show()
            },{
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
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    // ==== OLD INIT CODE ======
/*    val lensFacing =  CameraSelector.LENS_FACING_FRONT
    //else CameraSelector.LENS_FACING_BACK
    cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    getProcessCameraProvider()?.observe(
    this,
    Observer { provider: ProcessCameraProvider? ->
        cameraProvider = provider
        bindAllCameraUseCases()
    }
    )*/

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider?.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }


    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        val targetResolution = Size(480, 360)
        builder.setTargetResolution(targetResolution)
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(binding.previewView.surfaceProvider)
        cameraProvider!!.bindToLifecycle( viewLifecycleOwner,
            cameraSelector!!,
            previewUseCase
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }

        try {
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setMinFaceSize(0.15f)
                .enableTracking()
                .build()
            faceDetector = FaceDetection.getClient(options)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Can not create image processor: " + e.localizedMessage,
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val builder = ImageAnalysis.Builder()
        val targetResolution = Size(480, 360)
        builder.setTargetResolution(targetResolution)
        analysisUseCase = builder.build()

        analysisUseCase?.setAnalyzer(
            ContextCompat.getMainExecutor(requireContext())
        ) {
            detectDrowsiness(it)
        }
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner = */ viewLifecycleOwner,
            cameraSelector!!,
            analysisUseCase
        )
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun detectDrowsiness(imageProxy: ImageProxy)  {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        faceDetector.process(image)
            .addOnSuccessListener { faces ->
                for (face in faces) {
                    var rightEyeOpenProb = 0.0f
                    var leftEyeOpenProb = 0.0f
                    if (face.rightEyeOpenProbability != null) {
                        rightEyeOpenProb = face.rightEyeOpenProbability!!
                    }
                    if (face.leftEyeOpenProbability != null) {
                        leftEyeOpenProb = face.leftEyeOpenProbability!!
                    }
                    if ((rightEyeOpenProb <= 0.5 || leftEyeOpenProb <= 0.5)) {
                        if (!drowsinessDetected) {
                            drowsinessDetected = true
                            drowsyTime = System.currentTimeMillis()
                        }
                    } else {
                        drowsinessDetected = false
                       // dismissAlarm()
                        drowsinessAlertDialog.dismiss()
                    }

                      if(drowsinessDetected && (System.currentTimeMillis() - drowsyTime > drowsyThreshold)){
                         // triggerAlarm()
                          drowsinessAlertDialog.show()
                      }
                }
                imageProxy.close()
            }.addOnFailureListener {
                return@addOnFailureListener
            }
    }

    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (!isPermissionGranted(requireContext(), permission)) {
                return false
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = ArrayList<String>()
        for (permission in REQUIRED_PERMISSIONS) {
            if (!prefManager.getAutoStartEnabled())
                continue
            permission.let {
                if (!isPermissionGranted(requireContext(), it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissionsToRequest.toTypedArray(),
            permissionRequestCode
        )
    }


    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context, permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_camera
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != permissionRequestCode)
            return
        if (allRuntimePermissionsGranted()) {
            initViews()
        } else {
            Toast.makeText(
                requireContext(),
                "Permissions not granted by the user. You can manually change permissions from settings anytime",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun getProcessCameraProvider(): LiveData<ProcessCameraProvider>? {
        if (cameraProviderLiveData == null) {
            cameraProviderLiveData = MutableLiveData()
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener(
                {
                    try {
                        cameraProviderLiveData!!.setValue(cameraProviderFuture.get())
                    } catch (e: ExecutionException) {
                        // Handle any errors (including cancellation) here.
                        Log.e(TAG, "Unhandled exception", e)
                    } catch (e: InterruptedException) {
                        Log.e(TAG, "Unhandled exception", e)
                    }
                },
                ContextCompat.getMainExecutor(requireContext())
            )
        }
        return cameraProviderLiveData
    }
    /*    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if(item.itemId == R.id.setting_menu){
                val intent = Intent(this@CameraFragment, SettingsActivity::class.java)
                startActivity(intent)
            }
            return super.onOptionsItemSelected(item)
        }*/
}

