package com.hbeonlabs.driversalerts.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.ActivityWebRtcBinding
import org.webrtc.*


class WebRtcActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebRtcBinding
    private lateinit var rootEglBase: EglBase
    private lateinit var factory: PeerConnectionFactory

    var audioConstraints: MediaConstraints? = null
    var audioSource: AudioSource? = null
    var localAudioTrack: AudioTrack? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_web_rtc)
        initializeSurfaceViews()
        initializePeerConnectionFactory()
        createVideoTrackFromCamera1AndShowIt()
        createVideoTrackFromCamera2AndShowIt()
    }

    private fun initializeSurfaceViews() {
        this.rootEglBase = EglBase.create()
        binding.surfaceview1.init(this.rootEglBase.eglBaseContext,null)
        binding.surfaceview1.setEnableHardwareScaler(true)
        binding.surfaceview1.setMirror(true)
        binding.surfaceview2.init(this.rootEglBase.eglBaseContext,null)
        binding.surfaceview2.setEnableHardwareScaler(true)
        binding.surfaceview2.setMirror(true)
    }


    private fun initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true)
        factory = PeerConnectionFactory(null as PeerConnectionFactory.Options?)
        factory.setVideoHwAccelerationOptions(
            this.rootEglBase.eglBaseContext,
            this.rootEglBase.eglBaseContext
        )
    }

    private fun createVideoTrackFromCamera1AndShowIt() {
        this.audioConstraints = MediaConstraints()
        val videoCapturer: VideoCapturer? = createFrontCapturer(Camera1Enumerator(true))
        val videoSource: VideoSource = this.factory.createVideoSource(videoCapturer)
        videoCapturer?.startCapture(1280, 720, 30)
        val videoTrackFromCamera = this.factory.createVideoTrack("ARDAMSv0", videoSource)
        videoTrackFromCamera.setEnabled(true)
        videoTrackFromCamera.addRenderer(VideoRenderer(binding.surfaceview1))
        this.audioSource = this.factory.createAudioSource(this.audioConstraints)
        this.localAudioTrack = this.factory.createAudioTrack("101", this.audioSource)
    }

    private fun createVideoTrackFromCamera2AndShowIt() {
        //this.audioConstraints = MediaConstraints()
        val videoCapturer: VideoCapturer? = createBackCaptor(Camera1Enumerator(true))
        val videoSource: VideoSource = this.factory.createVideoSource(videoCapturer)
        videoCapturer?.startCapture(1280, 720, 30)
        val videoTrackFromCamera = this.factory.createVideoTrack("ARDAMSv1", videoSource)
        videoTrackFromCamera.setEnabled(true)
        videoTrackFromCamera.addRenderer(VideoRenderer(binding.surfaceview2))
        //this.audioSource = this.factory.createAudioSource(this.audioConstraints)
        //this.localAudioTrack = this.factory.createAudioTrack("101", this.audioSource)
    }

//    private fun createVideoCapturer(): VideoCapturer? {
//        val videoCapturer: VideoCapturer? = if (useCamera2()) {
//            createCameraCapturer(Camera2Enumerator(this))
//        } else {
//            createCameraCapturer(Camera1Enumerator(true))
//        }
//        return videoCapturer
//    }


    private fun createFrontCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        var var3 = deviceNames
        var var4 = deviceNames.size
        var var5: Int
        var deviceName: String?
        var videoCapturer: CameraVideoCapturer?
        var5 = 0
        while (var5 < var4) {
            deviceName = var3[var5]
            if (enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
            ++var5
        }
        var3 = deviceNames
        var4 = deviceNames.size
        var5 = 0
        while (var5 < var4) {
            deviceName = var3[var5]
            if (!enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
            ++var5
        }
        return null
    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(this)
    }

    private fun createBackCaptor(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        // First, try to find back facing camera
        for (deviceName in deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // back facing camera not found, try something else
        for (deviceName in deviceNames) {
            if (!enumerator.isBackFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

}