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
import com.hbeonlabs.driversalerts.utils.constants.AppConstants.CAMERA_PERMISSION_REQ_CODE
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CameraFragment : BaseFragment<FragmentCameraBinding>(), EasyPermissions.PermissionCallbacks,
    ActivityCompat.OnRequestPermissionsResultCallback {
    private var cameraProvider: ProcessCameraProvider? = null
    var alarmTriggered = false
    lateinit var drowsinessAlertDialog: Dialog
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    override fun initView() {
        super.initView()
        drowsinessAlertDialog = dialogDrowsinessAlert()
        askCameraPermission()
    }

    private fun askCameraPermission() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.CAMERA)) {
            doOnPermissionGranted()
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
        closeCameraRear()
        stopBackgroundThread()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_camera
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        askCameraPermission{
            doOnCameraPermissionGranted()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionDenied(this, perms.first())) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        } else {
            askCameraPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults)
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()
        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (binding.previewViewBack.isAvailable) {
            openCameraRear(binding.previewViewBack.width, binding.previewViewBack.height)
        } else {
            binding.previewViewBack.surfaceTextureListener = surfaceTextureListenerRear
        }
    }
    

    /**
     * Opens rear camera specified by [Camera2BasicFragment.cameraId].
     */
    private fun openCameraRear(width: Int, height: Int) {
        val permission = activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) }
        if (permission != PackageManager.PERMISSION_GRANTED) {
           askCameraPermission {  }
            return
        }
        setUpCameraOutputsRear(width, height)
        configureTransformRear(width, height)
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            // Wait for camera to open - 2.5 seconds is sufficient
            if (!cameraOpenCloseLockRear.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw RuntimeException("Time out waiting to lock camera opening.")
            }
            manager.openCamera(cameraIdRear, stateCallbackRear, backgroundHandlerRear)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera opening.", e)
        }

    }
    

    /**
     * Closes the current [CameraDevice].
     */
    private fun closeCameraRear() {
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                captureSessionRear?.stopRepeating();
                captureSessionRear?.abortCaptures();
            }
            cameraOpenCloseLockRear.acquire()
            captureSessionRear?.close()
            captureSessionRear = null
            cameraDeviceRear?.close()
            cameraDeviceRear = null
            imageReaderRear?.close()
            imageReaderRear = null
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock rear camera closing.", e)
        } finally {
            cameraOpenCloseLockRear.release()
        }
    }
    /**
     * Sets up member variables related to rear camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private fun setUpCameraOutputsRear(width: Int, height: Int) {
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)

                // We don't use a front facing camera in this sample.
                val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)
                if (cameraDirection != null &&
                    cameraDirection == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue
                }

                val map = characteristics.get(
                    CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

                // For still image captures, we use the largest available size.
                val aspectRatio = Collections.max(
                    Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)),
                    CompareSizesByViewAspectRatio(binding.previewViewBack.height, binding.previewViewBack.width)
                )
                imageReaderRear = ImageReader.newInstance(aspectRatio.width, aspectRatio.height,
                    ImageFormat.JPEG, /*maxImages*/ 2).apply {
                    setOnImageAvailableListener(onImageAvailableListenerRear, backgroundHandlerRear)
                }

                Log.d(TAG, "selected aspect ratio " + aspectRatio.height  + "x" + aspectRatio.width + " : " + aspectRatio.height/aspectRatio.width)
                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                val displayRotation = requireActivity().windowManager.defaultDisplay.rotation

                sensorOrientationRear = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
                val swappedDimensions = areDimensionsSwappedRear(displayRotation)

                val displaySize = Point()
                requireActivity().windowManager.defaultDisplay.getSize(displaySize)
                val rotatedPreviewWidth = if (swappedDimensions) height else width
                val rotatedPreviewHeight = if (swappedDimensions) width else height
                var maxPreviewWidth = if (swappedDimensions) displaySize.y else displaySize.x
                var maxPreviewHeight = if (swappedDimensions) displaySize.x else displaySize.y

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) maxPreviewWidth = MAX_PREVIEW_WIDTH
                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) maxPreviewHeight = MAX_PREVIEW_HEIGHT

                // Danger, W.R.! Attempting to use too large a preview size could exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                previewSizeRear = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
                    rotatedPreviewWidth, rotatedPreviewHeight,
                    maxPreviewWidth, maxPreviewHeight,
                    aspectRatio)


                /*
                 * We are filling the whole view with camera preview, on a downside, this distorts
                 * the aspect ratio.
                 * To retain the aspect ratio, uncomment the below line.
                 * Another option is the crop preview into the view, for that we have to choose
                 * preview ratio such that it comes nearest to aspect ratio of view.
                 */
//                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    textureView.setAspectRatio(previewSize.width, previewSize.height)
//                } else {
//                    textureView.setAspectRatio(previewSize.height, previewSize.width)
//                }

                this.cameraIdRear = cameraId

                // We've found a viable camera and finished setting up member variables,
                // so we don't need to iterate through other available cameras.
                return
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
          /*  ErrorDialog.newInstance(getString(R.string.camera_error))
                .show(childFragmentManager, FRAGMENT_DIALOG)*/
        }

    }

    /**
     * Configures the necessary [android.graphics.Matrix] transformation to `textureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `textureView` is fixed.
     *
     * @param viewWidth  The width of `textureView`
     * @param viewHeight The height of `textureView`
     */
    private fun configureTransformRear(viewWidth: Int, viewHeight: Int) {
        activity ?: return
        val rotation = requireActivity().windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
        val bufferRect = RectF(0f, 0f, previewSizeRear.height.toFloat(), previewSizeRear.width.toFloat())
        val centerX = viewRect.centerX()
        val centerY = viewRect.centerY()

        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
            val scale = Math.max(
                viewHeight.toFloat() / previewSizeRear.height,
                viewWidth.toFloat() / previewSizeRear.width)
            with(matrix) {
                setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
                postScale(scale, scale, centerX, centerY)
                postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
            }
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180f, centerX, centerY)
        }
        binding.previewViewBack.setTransform(matrix)
    }
    

    /**
     * Determines if the dimensions are swapped given the phone's current rotation.
     *
     * @param displayRotation The current rotation of the display
     *
     * @return true if the dimensions are swapped, false otherwise.
     */
    private fun areDimensionsSwappedRear(displayRotation: Int): Boolean {
        var swappedDimensions = false
        when (displayRotation) {
            Surface.ROTATION_0, Surface.ROTATION_180 -> {
                if (sensorOrientationRear == 90 || sensorOrientationRear == 270) {
                    swappedDimensions = true
                }
            }
            Surface.ROTATION_90, Surface.ROTATION_270 -> {
                if (sensorOrientationRear == 0 || sensorOrientationRear == 180) {
                    swappedDimensions = true
                }
            }
            else -> {
                Log.e(TAG, "Display rotation is invalid: $displayRotation")
            }
        }
        return swappedDimensions
    }

    /**
     * Starts a background thread and its [Handler].
     */
    private fun startBackgroundThread() {
        backgroundThreadRear = HandlerThread("CameraBackgroundRear").also { it.start() }
        backgroundHandlerRear = backgroundThreadRear?.looper?.let { Handler(it) }
    }

    /**
     * Stops the background thread and its [Handler].
     */
    private fun stopBackgroundThread() {
        backgroundThreadRear?.quitSafely()
        try {

            backgroundThreadRear?.join()
            backgroundThreadRear = null
            backgroundHandlerRear = null
        } catch (e: InterruptedException) {
            Log.e(TAG, e.toString())
        }

    }


    /**
     * Creates a new [CameraCaptureSession] for rear camera preview.
     */
    private fun createCameraPreviewSessionRear() {
        try {
            val texture = binding.previewViewBack.surfaceTexture

            // We configure the size of default buffer to be the size of camera preview we want.
            texture?.setDefaultBufferSize(previewSizeRear.width, previewSizeRear.height)

            // This is the output Surface we need to start preview.
            val surface = Surface(texture)

            // We set up a CaptureRequest.Builder with the output Surface.
            previewRequestBuilderRear = cameraDeviceRear!!.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )
            previewRequestBuilderRear.addTarget(surface)

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDeviceRear?.createCaptureSession(
                Arrays.asList(surface, imageReaderRear?.surface),
                object : CameraCaptureSession.StateCallback() {

                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        // The camera is already closed
                        if (cameraDeviceRear == null) return

                        // When the session is ready, we start displaying the preview.
                        captureSessionRear = cameraCaptureSession
                        try {
                            // Auto focus should be continuous for camera preview.
                            previewRequestBuilderRear.set(CaptureRequest.CONTROL_AF_MODE,
                                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)

                            // Finally, we start displaying the camera preview.
                            previewRequestRear = previewRequestBuilderRear.build()
                            captureSessionRear?.setRepeatingRequest(previewRequestRear,
                                captureCallback, backgroundHandlerRear)
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, e.toString())
                        }

                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
//                        activity.showToast("Failed")
                        Log.d(TAG, "CaptureSession failed")
                    }
                }, null)
        } catch (e: CameraAccessException) {
            Log.e(TAG, e.toString())
        }

    }


    companion object {

        /**
         * Conversion from screen rotation to JPEG orientation.
         */
        private val ORIENTATIONS = SparseIntArray()
        private val FRAGMENT_DIALOG = "dialog"

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        /**
         * Max preview width that is guaranteed by Camera2 API
         */
        private val MAX_PREVIEW_WIDTH = 1920

        /**
         * Max preview height that is guaranteed by Camera2 API
         */
        private val MAX_PREVIEW_HEIGHT = 1080


        /**
         * Given `choices` of `Size`s supported by a camera, choose the smallest one that
         * is at least as large as the respective texture view size, and that is at most as large as
         * the respective max size, and whose aspect ratio matches with the specified value. If such
         * size doesn't exist, choose the largest one that is at most as large as the respective max
         * size, and whose aspect ratio matches with the specified value.
         *
         * @param choices           The list of sizes that the camera supports for the intended
         *                          output class
         * @param textureViewWidth  The width of the texture view relative to sensor coordinate
         * @param textureViewHeight The height of the texture view relative to sensor coordinate
         * @param maxWidth          The maximum width that can be chosen
         * @param maxHeight         The maximum height that can be chosen
         * @param aspectRatio       The aspect ratio
         * @return The optimal `Size`, or an arbitrary one if none were big enough
         */
        @JvmStatic private fun chooseOptimalSize(
            choices: Array<Size>,
            textureViewWidth: Int,
            textureViewHeight: Int,
            maxWidth: Int,
            maxHeight: Int,
            aspectRatio: Size
        ): Size {

            // Collect the supported resolutions that are at least as big as the preview Surface
            val bigEnough = ArrayList<Size>()
            // Collect the supported resolutions that are smaller than the preview Surface
            val notBigEnough = ArrayList<Size>()
            val w = aspectRatio.width
            val h = aspectRatio.height
            for (option in choices) {
                if (option.width <= maxWidth && option.height <= maxHeight &&
                    option.height == option.width * h / w) {
                    if (option.width >= textureViewWidth && option.height >= textureViewHeight) {
                        bigEnough.add(option)
                    } else {
                        notBigEnough.add(option)
                    }
                }
            }

            // Pick the smallest of those big enough. If there is no one big enough, pick the
            // largest of those not big enough.
            if (bigEnough.size > 0) {
                return Collections.min(bigEnough, CompareSizesByViewAspectRatio(textureViewHeight, textureViewWidth))
            } else if (notBigEnough.size > 0) {
                return Collections.max(notBigEnough, CompareSizesByViewAspectRatio(textureViewHeight, textureViewWidth))
            } else {
                Log.e(TAG, "Couldn't find any suitable preview size")
                return choices[0]
            }
        }

        /**
         * Tag for the [Log] from this class
         */
        private val TAG = CameraFragment::class.java.simpleName

        @JvmStatic fun newInstance(): CameraFragment = CameraFragment()
    }




}

