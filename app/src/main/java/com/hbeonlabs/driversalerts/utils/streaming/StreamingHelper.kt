package com.hbeonlabs.driversalerts.utils.streaming

import android.app.Activity
import android.util.Log
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
    val errorTag = "StreamingHelperError"
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
        val useApisToCreateRoomAndToken = true
        if(useApisToCreateRoomAndToken) {
            viewModel.createRoom()
            viewModel.createRoomLiveData.observe(lifecycleOwner) {
                if (it?.message?.startsWith("Error") == true) {
                    showError("Room error ${it.message}")
                } else {
                    viewModel.createToken(FRONT_ROOM, "FrontSender2")
                    viewModel.createToken(BACK_ROOM, "BackSender2")
                }
            }
            viewModel.createTokenLiveData.observe(lifecycleOwner) {
                it?.let {
                    if (it.data?.token != null) {
                        if (it.roomName?.startsWith(FRONT_ROOM) == true) {
                            startFrontRoom(lifecycleScope, it.data.token)
                        } else if (it.roomName?.startsWith(BACK_ROOM) == true) {
                            startBackRoom(lifecycleScope, it.data.token)
                        }
                    }else {
                        showError("Token error ${it.message}")
                    }
                }
            }
        } else {
            startFrontRoom(lifecycleScope,"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzaGEyNTYiOiIiLCJtZXRhZGF0YSI6ImRyaXZlciIsInZpZGVvIjp7InJvb21DcmVhdGUiOnRydWUsInJvb21Kb2luIjp0cnVlLCJyb29tIjoiRnJvbnRSb29tMSIsImNhblB1Ymxpc2giOnRydWUsImNhblN1YnNjcmliZSI6dHJ1ZSwicm9vbVJlY29yZCI6dHJ1ZSwiY2FuUHVibGlzaFNvdXJjZXMiOlsiY2FtZXJhIiwibWljcm9waG9uZSJdfSwiaWF0IjoxNjk2OTk4Nzg0LCJuYmYiOjE2OTY5OTg3ODQsImV4cCI6MTY5NzM0NDM4NCwiaXNzIjoiQVBJa1hkZWV2S1JiTHVaIiwic3ViIjoiRnJvbnRTZW5kZXIxIiwianRpIjoiRnJvbnRTZW5kZXIxIn0.aDfpbfV1dMpdlri0_Yxfl97VAWNRBCUfva_7DUUqa0k")
            startBackRoom(lifecycleScope,"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzaGEyNTYiOiIiLCJtZXRhZGF0YSI6ImRyaXZlciIsInZpZGVvIjp7InJvb21DcmVhdGUiOnRydWUsInJvb21Kb2luIjp0cnVlLCJyb29tIjoiQmFja1Jvb20xIiwiY2FuUHVibGlzaCI6dHJ1ZSwiY2FuU3Vic2NyaWJlIjp0cnVlLCJyb29tUmVjb3JkIjp0cnVlLCJjYW5QdWJsaXNoU291cmNlcyI6WyJjYW1lcmEiLCJtaWNyb3Bob25lIl19LCJpYXQiOjE2OTY5OTg3NjAsIm5iZiI6MTY5Njk5ODc2MCwiZXhwIjoxNjk3MzQ0MzYwLCJpc3MiOiJBUElrWGRlZXZLUmJMdVoiLCJzdWIiOiJCYWNrU2VuZGVyMSIsImp0aSI6IkJhY2tTZW5kZXIxIn0.BdJ7t--RaLR73-wRW42-vH5tng-nbHR55ZKSuTlMq2k")
        }
    }

    private fun showError(error:String){
        Log.v(errorTag,error)
        frontStreamingStatus = error
        backStreamingStatus = error
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
            Log.v(errorTag,"connectToRoomForFront error ${e.message}")
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
            Log.v(errorTag,"connectToRoomForBack error ${e.message}")
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