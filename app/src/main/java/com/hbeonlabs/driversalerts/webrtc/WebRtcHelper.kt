package com.hbeonlabs.driversalerts.webrtc

import android.content.Context
import org.webrtc.*
import org.webrtc.EglRenderer.FrameListener

class WebRtcHelper {
    private var rootEglBase: EglBase = EglBase.create()
    private lateinit var surfaceView1: SurfaceViewRenderer
    private lateinit var surfaceView2: SurfaceViewRenderer
    var audioConstraints: MediaConstraints? = null
    var audioSource: AudioSource? = null
    var localAudioTrack: AudioTrack? = null
    private lateinit var videoTrackFromCamera: VideoTrack
    private lateinit var factory: PeerConnectionFactory
    val VIDEO_TRACK_ID = "ARDAMSv0"
    val VIDEO_RESOLUTION_WIDTH = 1280
    val VIDEO_RESOLUTION_HEIGHT = 720
    val FPS = 30
    lateinit var context : Context

    fun onCreate(context : Context, surfaceView1: SurfaceViewRenderer, surfaceView2: SurfaceViewRenderer, frameListener: FrameListener) {
        this.context = context
        this.surfaceView1 = surfaceView1
        this.surfaceView2 = surfaceView2
        initializeSurfaceViews(frameListener)
        initializePeerConnectionFactory()
        createVideoTrackFromCameraAndShowIt()
    }
    private fun initializeSurfaceViews(frameListener: FrameListener) {
        rootEglBase = EglBase.create()
        surfaceView1.init(rootEglBase.eglBaseContext, null)
        surfaceView1.setEnableHardwareScaler(true)
        surfaceView1.setMirror(true)
        surfaceView1.addFrameListener(frameListener,1.0f)
//        surfaceView2.init(rootEglBase.eglBaseContext, null)
//        surfaceView2.setEnableHardwareScaler(true)
//        surfaceView2.setMirror(true)
    }

    fun addFrameListener(frameListener: FrameListener){
        surfaceView1.addFrameListener(frameListener,1.0f)
    }
    private fun initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true)
        factory = PeerConnectionFactory(null)
        factory.setVideoHwAccelerationOptions(
            rootEglBase.eglBaseContext,
            rootEglBase.eglBaseContext
        )
    }

    private fun createVideoTrackFromCameraAndShowIt() {
        audioConstraints = MediaConstraints()
        val videoCapturer = createVideoCapturer()
        val videoSource = factory.createVideoSource(videoCapturer)
        videoCapturer?.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS)

        videoTrackFromCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource)
        videoTrackFromCamera.setEnabled(true)
        videoTrackFromCamera.addRenderer(VideoRenderer(surfaceView1))

        //create an AudioSource instance
        audioSource = factory.createAudioSource(audioConstraints)
        localAudioTrack = factory.createAudioTrack("101", audioSource)
    }

    private fun createVideoCapturer(): VideoCapturer? {
        val videoCapturer: VideoCapturer?
        videoCapturer = if (useCamera2()) {
            createCameraCapturer(Camera2Enumerator(context))
        } else {
            createCameraCapturer(Camera1Enumerator(true))
        }
        return videoCapturer
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    private fun useCamera2(): Boolean {
        return Camera2Enumerator.isSupported(context)
    }

}