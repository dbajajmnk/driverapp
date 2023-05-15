package com.hbeonlabs.driversalerts.utils.streaming

import android.app.Activity
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import io.livekit.android.LiveKit
import io.livekit.android.LiveKitOverrides
import io.livekit.android.RoomOptions
import io.livekit.android.audio.AudioSwitchHandler
import io.livekit.android.renderer.SurfaceViewRenderer
import io.livekit.android.room.Room
import io.livekit.android.room.participant.AudioTrackPublishDefaults
import io.livekit.android.room.participant.LocalParticipant
import io.livekit.android.room.participant.VideoTrackPublishDefaults
import io.livekit.android.room.track.CameraPosition
import io.livekit.android.room.track.DataPublishReliability
import io.livekit.android.room.track.LocalAudioTrackOptions
import io.livekit.android.room.track.LocalVideoTrackOptions
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoPreset169
import io.livekit.android.room.track.VideoTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class StreamingHelper(private val context : Activity, private val frontRenderer : SurfaceViewRenderer,private val backRenderer : SurfaceViewRenderer) {


    private var backVideoTrack: VideoTrack? = null
    private var frontVideoTrack: VideoTrack? = null
    private lateinit var frontRoom: Room
    private lateinit var backRoom: Room
    private var frontStreamingStatus = "Connecting..."
    private var backStreamingStatus = "Connecting..."
    init {
        initFrontRoom()
        initBackRoom()
        frontRoom.initVideoRenderer(frontRenderer)
        backRoom.initVideoRenderer(backRenderer)
        frontRenderer.setMirror(true)
    }

    fun startStreaming(lifecycleScope: CoroutineScope){
        lifecycleScope.launch {
            connectToRoomForFront()
            connectToRoomForBack()
        }
    }

    private fun initFrontRoom() {
        val audioHandler = AudioSwitchHandler(context)
        frontRoom = LiveKit.create(
            appContext = context.applicationContext,
            options = getRoomOptions(CameraPosition.FRONT),
            overrides = LiveKitOverrides(
                audioHandler = audioHandler
            )
        )
    }

    private fun initBackRoom() {
        val audioHandler = AudioSwitchHandler(context)
        backRoom = LiveKit.create(
            appContext = context.applicationContext,
            options = getRoomOptions(CameraPosition.BACK),
            overrides = LiveKitOverrides(
                audioHandler = audioHandler
            )
        )
    }

    private suspend fun connectToRoomForFront() {
        try {
            frontStreamingStatus = "Connecting..."
            frontRoom.connect(
                url = "wss://flexigigs.co",
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2aWRlbyI6eyJyb29tSm9pbiI6dHJ1ZSwicm9vbSI6IkZyb250Q2FtZXJhIiwiY2FuUHVibGlzaCI6dHJ1ZSwiY2FuU3Vic2NyaWJlIjp0cnVlfSwiaWF0IjoxNjgzOTc0ODQ4LCJuYmYiOjE2ODM5NzQ4NDgsImV4cCI6MTc0NzA0Njg0OCwiaXNzIjoiQVBJWDllTG5HZmllS1k4Iiwic3ViIjoiRnJvbnRTZW5kZXIiLCJqdGkiOiJGcm9udFNlbmRlciJ9.8sXG7QANLNsh8rmwkWf5m7Z73qi3eaYvHZRRl0_Pgb0",
            )
            frontStreamingStatus = "Connected"
            // Create and publish audio/video tracks
            val localParticipant = frontRoom.localParticipant
            localParticipant.setMicrophoneEnabled(true)
            localParticipant.setCameraEnabled(true)

            showFrontCameraView(localParticipant)
            frontStreamingStatus = "Started"
        } catch (e: Throwable) {
            frontStreamingStatus = e.message ?: "Error"
            e.printStackTrace()
        }
    }

    private suspend fun connectToRoomForBack() {
        try {
            backStreamingStatus = "Connecting..."
            backRoom.connect(
                url = "wss://flexigigs.co",
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2aWRlbyI6eyJyb29tSm9pbiI6dHJ1ZSwicm9vbSI6IkJhY2tDYW1lcmEiLCJjYW5QdWJsaXNoIjp0cnVlLCJjYW5TdWJzY3JpYmUiOnRydWV9LCJpYXQiOjE2ODM5NzU4NTcsIm5iZiI6MTY4Mzk3NTg1NywiZXhwIjoxNzQ3MDQ3ODU3LCJpc3MiOiJBUElYOWVMbkdmaWVLWTgiLCJzdWIiOiJCYWNrU2VuZGVyIiwianRpIjoiQmFja1NlbmRlciJ9.MxvRdaqJCPJzOxq03MJTSfWCMABU9MJzdgLsWYIsoC8",
            )
            backStreamingStatus = "Connected"
            // Create and publish audio/video tracks
            val localParticipant = backRoom.localParticipant
            localParticipant.setMicrophoneEnabled(true)
            localParticipant.setCameraEnabled(true)

            showBackCameraView(localParticipant)
            backStreamingStatus = "Started"
        } catch (e: Throwable) {
            backStreamingStatus = e.message ?: "Error"
            e.printStackTrace()
        }
    }

    private fun showFrontCameraView(participant: LocalParticipant) {
        val trackPublication = participant.getTrackPublication(Track.Source.CAMERA)
        val videoTrack = trackPublication?.track as VideoTrack
        setupFrontVideo(videoTrack)
    }

    private fun showBackCameraView(participant: LocalParticipant) {
        val trackPublication = participant.getTrackPublication(Track.Source.CAMERA)
        val videoTrack = trackPublication?.track as VideoTrack
        setupBackVideo(videoTrack)
    }

    private fun setupFrontVideo(videoTrack: VideoTrack?) {
        if (frontVideoTrack == videoTrack) {
            return
        }
        frontVideoTrack?.removeRenderer(frontRenderer)
        frontVideoTrack = videoTrack
        frontVideoTrack?.addRenderer(frontRenderer)
    }

    private fun setupBackVideo(videoTrack: VideoTrack?) {
        if (backVideoTrack == videoTrack) {
            return
        }
        backVideoTrack?.removeRenderer(backRenderer)
        backVideoTrack = videoTrack
        backVideoTrack?.addRenderer(backRenderer)
    }

    private fun removeRenderers() {
        backVideoTrack?.let {
            backVideoTrack?.removeRenderer(backRenderer)
            backVideoTrack = null
        }
        frontVideoTrack?.let {
            frontVideoTrack?.removeRenderer(frontRenderer)
            frontVideoTrack = null
        }
    }

    fun disconnect() {
        removeRenderers()
    }

    private fun getRoomOptions(cameraPosition: CameraPosition): RoomOptions {
        return RoomOptions(
            audioTrackCaptureDefaults = LocalAudioTrackOptions(
                noiseSuppression = true,
                echoCancellation = true,
                autoGainControl = true,
                highPassFilter = true,
                typingNoiseDetection = true,
            ),
            videoTrackCaptureDefaults = LocalVideoTrackOptions(
                deviceId = "",
                position = cameraPosition,
                captureParams = VideoPreset169.HD.capture,
            ),
            audioTrackPublishDefaults = AudioTrackPublishDefaults(
                audioBitrate = 20_000,
                dtx = true,
            ),
            videoTrackPublishDefaults = VideoTrackPublishDefaults(
                videoEncoding = VideoPreset169.HD.encoding,
            ),
            adaptiveStream = true, dynacast = true
        )
    }

    fun getFrontStreamingStatus() = frontStreamingStatus
    fun getBackStreamingStatus() = backStreamingStatus

    suspend fun sendLocation(locationData: LocationAndSpeed){
        val data = "${locationData.locationLatitude}//${locationData.locationLongitude}//${locationData.speed}"
        frontRoom.localParticipant.publishData(data.toByteArray(), DataPublishReliability.LOSSY)
    }
}