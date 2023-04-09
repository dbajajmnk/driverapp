package com.hbeonlabs.driversalerts.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

class DrowsinessDetector(
    private val onDrowsinessDetected: () -> Unit,
    private val onDrowsinessGone: () -> Unit,
) : ImageAnalysis.Analyzer {

    private var faceDetector: FaceDetector
    private var drowsyTime = 0L
    private var drowsyThreshold = 1000
    private var drowsinessDetected = false

    init {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
        faceDetector = FaceDetection.getClient(options)
    }

    override fun analyze(image: ImageProxy) {
        detectDrowsiness(image)
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun detectDrowsiness(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        detectDrowsiness(image)
        imageProxy.close()
    }


    @SuppressLint("UnsafeOptInUsageError")
    fun detectDrowsiness(image: InputImage) {
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
                        onDrowsinessGone()
                    }

                    if (drowsinessDetected && (System.currentTimeMillis() - drowsyTime > drowsyThreshold)) {
                        // triggerAlarm()
                        onDrowsinessDetected()
                    }
                }
            }.addOnFailureListener {
                return@addOnFailureListener
            }
    }
}