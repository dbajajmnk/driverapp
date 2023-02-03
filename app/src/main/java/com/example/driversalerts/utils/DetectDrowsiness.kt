package com.example.driversalerts.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector

class DrowsinessAnalyser(
    private val onDrowsinessDetected:()->Unit,
    private val onDrowsinessGone:()->Unit,
):ImageAnalysis.Analyzer {

    private lateinit var faceDetector: FaceDetector
    private var drowsyTime = 0L
    private var drowsyThreshold = 1000
    var drowsinessDetected = false

    override fun analyze(image: ImageProxy) {
        detectDrowsiness(image)
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun detectDrowsiness(imageProxy: ImageProxy)  {
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
                       onDrowsinessGone()
                    }

                    if(drowsinessDetected && (System.currentTimeMillis() - drowsyTime > drowsyThreshold)){
                        // triggerAlarm()
                      onDrowsinessDetected()
                    }
                }
                imageProxy.close()
            }.addOnFailureListener {
                return@addOnFailureListener
            }
    }
}