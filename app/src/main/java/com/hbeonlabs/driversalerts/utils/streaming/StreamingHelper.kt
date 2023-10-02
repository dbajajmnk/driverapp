package com.hbeonlabs.driversalerts.utils.streaming

import android.app.Activity
import androidx.lifecycle.LifecycleOwner
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.ui.fragment.dashboard.DashboardViewModel
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

private const val FRONT_ROOM = "FrontRoom"

private const val BACK_ROOM = "BackRoom"

class StreamingHelper(private val context : Activity, private val viewModel: DashboardViewModel, private val frontRenderer : SurfaceViewRenderer, private val backRenderer : SurfaceViewRenderer) {


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

    /**
     * Create room and generate token and start streaming after that
     */
    fun startStreaming(lifecycleScope: CoroutineScope, lifecycleOwner: LifecycleOwner){
        val useApisToCreateRoomAndToken = false
        if(useApisToCreateRoomAndToken) {
            viewModel.createRoom(FRONT_ROOM)
            viewModel.createRoomLiveData.observe(lifecycleOwner) {
                if (it?.message != null) {
                    viewModel.createToken(FRONT_ROOM, "FrontSender")
                    viewModel.createToken(BACK_ROOM, "BackSender")
                } else {
                    frontStreamingStatus = it?.message ?: "Room creation error"
                    backStreamingStatus = it?.message ?: "Room creation error"
                }
            }
            viewModel.createTokenLiveData.observe(lifecycleOwner) {
                it?.let {
                    if (it.message != null) {
                        if (it.roomName?.startsWith(FRONT_ROOM) == true) {
                            startFrontRoom(lifecycleScope, it.message)
                        } else if (it.roomName?.startsWith(BACK_ROOM) == true) {
                            startBackRoom(lifecycleScope, it.message)
                        }
                    }else {
                        frontStreamingStatus = "Token error ${it.data}"
                        backStreamingStatus = "Token error ${it.data}"
                    }
                }
            }
        } else {
            startFrontRoom(lifecycleScope,"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2aWRlbyI6eyJyb29tQ3JlYXRlIjp0cnVlLCJyb29tSm9pbiI6dHJ1ZSwicm9vbSI6InJvb20xIiwiY2FuUHVibGlzaCI6dHJ1ZSwiY2FuU3Vic2NyaWJlIjp0cnVlLCJyb29tUmVjb3JkIjp0cnVlfSwiaWF0IjoxNjk2MTQ2MjM5LCJuYmYiOjE2OTYxNDYyMzksImV4cCI6MTY5NjE2NzgzOSwiaXNzIjoiQVBJa1hkZWV2S1JiTHVaIiwic3ViIjoiVXNlcjUiLCJqdGkiOiJVc2VyNSJ9.53l64Sz2wnZZy5AIhEAYirRCHpcnunG3_c3aW3LfUzU")
            startBackRoom(lifecycleScope,"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2aWRlbyI6eyJyb29tQ3JlYXRlIjp0cnVlLCJyb29tSm9pbiI6dHJ1ZSwicm9vbSI6InJvb20yIiwiY2FuUHVibGlzaCI6dHJ1ZSwiY2FuU3Vic2NyaWJlIjp0cnVlLCJyb29tUmVjb3JkIjp0cnVlfSwiaWF0IjoxNjk2MTQ2Mjc1LCJuYmYiOjE2OTYxNDYyNzUsImV4cCI6MTY5NjE2Nzg3NSwiaXNzIjoiQVBJa1hkZWV2S1JiTHVaIiwic3ViIjoiVXNlcjYiLCJqdGkiOiJVc2VyNiJ9.71NXo4iApz4n7GC7J7FiFG4NgGpQ_K5C8Y7rtb2I4og")
        }
    }

    private fun initFrontRoom() {
        val audioHandler = AudioSwitchHandler(context)
        frontRoom = LiveKit.create(
            appContext = context.applicationContext,
            options = getRoomOptions(CameraPosition.FRONT, FRONT_ROOM),
            overrides = LiveKitOverrides(
                audioHandler = audioHandler
            )
        )
    }

    private fun initBackRoom() {
        val audioHandler = AudioSwitchHandler(context)
        backRoom = LiveKit.create(
            appContext = context.applicationContext,
            options = getRoomOptions(CameraPosition.BACK, BACK_ROOM),
            overrides = LiveKitOverrides(
                audioHandler = audioHandler
            )
        )
    }

    private fun startFrontRoom(lifecycleScope: CoroutineScope, token : String){
        frontStreamingStatus = "FrontStreamingStarted"
        lifecycleScope.launch {
            connectToRoomForFront(token)
        }
    }
    private fun startBackRoom(lifecycleScope: CoroutineScope, token : String){
        backStreamingStatus = "BackStreamingStarted"
        lifecycleScope.launch {
            connectToRoomForBack(token)
        }
    }
    private suspend fun connectToRoomForFront(token : String) {
        try {
            frontStreamingStatus = "Connecting to front room..."
            frontRoom.connect(
                url = "wss://webrtc.myschoolbus.in/",
                token = token
            )
            frontStreamingStatus = "Connected"
            // Create and publish audio/video tracks
            val localParticipant = frontRoom.localParticipant
            localParticipant.setMicrophoneEnabled(true)
            localParticipant.setCameraEnabled(true)
            localParticipant.name = "FrontSender"
            showFrontCameraView(localParticipant)
            frontStreamingStatus = "Started"
        } catch (e: Throwable) {
            frontStreamingStatus = e.message ?: "Error"
            e.printStackTrace()
        }
    }

    private suspend fun connectToRoomForBack(token : String) {
        try {
            backStreamingStatus = "Connecting to back room..."
            backRoom.connect(
                url = "wss://webrtc.myschoolbus.in/",
                token = token
            )
            backStreamingStatus = "Connected"
            // Create and publish audio/video tracks
            val localParticipant = backRoom.localParticipant
            localParticipant.setMicrophoneEnabled(true)
            localParticipant.setCameraEnabled(true)
            localParticipant.name = "BackSender"
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

    private fun getRoomOptions(cameraPosition: CameraPosition, deviceId : String): RoomOptions {
        return RoomOptions(
            audioTrackCaptureDefaults = LocalAudioTrackOptions(
                noiseSuppression = true,
                echoCancellation = true,
                autoGainControl = true,
                highPassFilter = true,
                typingNoiseDetection = true,
            ),
            videoTrackCaptureDefaults = LocalVideoTrackOptions(
                deviceId = deviceId,
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
        try {
            val data = "${locationData.locationLatitude}//${locationData.locationLongitude}//${locationData.speed}"
            frontRoom.localParticipant.publishData(data.toByteArray(), DataPublishReliability.LOSSY)
            println("Sending location = $locationData")
        }catch (e:Exception){
            e.printStackTrace()
            println("Error in sending location = ${e.message}")
        }
    }
}