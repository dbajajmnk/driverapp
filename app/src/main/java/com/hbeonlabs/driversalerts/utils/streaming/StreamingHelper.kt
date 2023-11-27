package com.hbeonlabs.driversalerts.utils.streaming

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.persistance.PrefManager
import com.hbeonlabs.driversalerts.ui.fragment.dashboard.DashboardViewModel
import com.hbeonlabs.driversalerts.utils.Utils
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


class StreamingHelper(private val context : Activity, private val viewModel: DashboardViewModel, private val frontRenderer : SurfaceViewRenderer, private val backRenderer : SurfaceViewRenderer) {

    private val FRONT_ROOM :String
    private val BACK_ROOM :String
    private var backVideoTrack: VideoTrack? = null
    private var frontVideoTrack: VideoTrack? = null
    private lateinit var frontRoom: Room
    private lateinit var backRoom: Room
    private var frontStreamingStatus = "Connecting..."
    private var backStreamingStatus = "Connecting..."
    val errorTag = "StreamingHelperError"
    private var prefManager : PrefManager
    private var isFrontCameraStarted = false
    private var isBackCameraStarted = false
    init {
        initFrontRoom()
        initBackRoom()
        frontRoom.initVideoRenderer(frontRenderer)
        backRoom.initVideoRenderer(backRenderer)
        frontRenderer.setMirror(true)
        prefManager = PrefManager(context)
        FRONT_ROOM = Utils.getRoomName(prefManager.getDeviceConfigurationDetails(),true)
        BACK_ROOM = Utils.getRoomName(prefManager.getDeviceConfigurationDetails(),false)
    }

    /**
     * Create room and generate token and start streaming after that
     */
    fun startStreaming(lifecycleScope: CoroutineScope, lifecycleOwner: LifecycleOwner){
        val useApisToCreateRoomAndToken = true
        if(useApisToCreateRoomAndToken) {
            viewModel.createToken(FRONT_ROOM, "FrontSender2")
            viewModel.createToken(BACK_ROOM, "BackSender2")
            viewModel.createTokenLiveData.observe(lifecycleOwner) {
                it?.let {
                    if (it.data?.token != null) {
                        Log.v(errorTag,"Room ${it.roomName} token ${it.data?.token}")
                        if (it.roomName?.startsWith(FRONT_ROOM) == true && !isFrontCameraStarted) {
                            isFrontCameraStarted = true
                            startFrontRoom(lifecycleScope, it.data.token)
                        } else if (it.roomName?.startsWith(BACK_ROOM) == true && !isBackCameraStarted) {
                            isBackCameraStarted = true
                            startBackRoom(lifecycleScope, it.data.token)
                        }
                    }else {
                        showError("Token error ${it.message}")
                    }
                }
            }
        } else {
            startFrontRoom(lifecycleScope,"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzaGEyNTYiOiJUcjB1YjRkb3ImM1hxdTFzMXQzbHlNeXN0M3Ixb3VzSzN5Q3IzQHQxMG4hIiwibWV0YWRhdGEiOiJkcml2ZXIiLCJ2aWRlbyI6eyJyb29tQ3JlYXRlIjp0cnVlLCJyb29tSm9pbiI6dHJ1ZSwicm9vbSI6IkZyb250Um9vbTIiLCJjYW5QdWJsaXNoIjp0cnVlLCJjYW5TdWJzY3JpYmUiOnRydWUsInJvb21SZWNvcmQiOnRydWUsImNhblB1Ymxpc2hTb3VyY2VzIjpbImNhbWVyYSIsIm1pY3JvcGhvbmUiXX0sImlhdCI6MTY5ODE0ODkzOSwibmJmIjoxNjk4MTQ4OTM5LCJleHAiOjE2OTgxNzA1MzksImlzcyI6IkFQSWtYZGVldktSYkx1WiIsInN1YiI6IlBhcnRpY2lwYW50TmFtZTUiLCJqdGkiOiJQYXJ0aWNpcGFudE5hbWU1In0.YAIxioPl-ly87zoMggD21vzCLFUz-IvIkjv8Re64uTA")
            startBackRoom(lifecycleScope,"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzaGEyNTYiOiJUcjB1YjRkb3ImM1hxdTFzMXQzbHlNeXN0M3Ixb3VzSzN5Q3IzQHQxMG4hIiwibWV0YWRhdGEiOiJkcml2ZXIiLCJ2aWRlbyI6eyJyb29tQ3JlYXRlIjp0cnVlLCJyb29tSm9pbiI6dHJ1ZSwicm9vbSI6IkJhY2tSb29tMiIsImNhblB1Ymxpc2giOnRydWUsImNhblN1YnNjcmliZSI6dHJ1ZSwicm9vbVJlY29yZCI6dHJ1ZSwiY2FuUHVibGlzaFNvdXJjZXMiOlsiY2FtZXJhIiwibWljcm9waG9uZSJdfSwiaWF0IjoxNjk4MTQ4OTYyLCJuYmYiOjE2OTgxNDg5NjIsImV4cCI6MTY5ODE3MDU2MiwiaXNzIjoiQVBJa1hkZWV2S1JiTHVaIiwic3ViIjoiUGFydGljaXBhbnROYW1lNiIsImp0aSI6IlBhcnRpY2lwYW50TmFtZTYifQ.PoQ8g7RQe96_05WkUg33gPcD5BzrVHFl_mp8T43JD08")
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
            options = getRoomOptions(CameraPosition.FRONT, "FRONT"),
            overrides = LiveKitOverrides(
                audioHandler = audioHandler
            )
        )
    }

    private fun initBackRoom() {
        val audioHandler = AudioSwitchHandler(context)
        backRoom = LiveKit.create(
            appContext = context.applicationContext,
            options = getRoomOptions(CameraPosition.BACK, "BACK"),
            overrides = LiveKitOverrides(
                audioHandler = audioHandler
            )
        )
    }

    private fun startFrontRoom(lifecycleScope: CoroutineScope, token : String){
        Log.v(errorTag,"startFrontRoom")
        frontStreamingStatus = "FrontStreamingStarted"
        lifecycleScope.launch {
            connectToRoomForFront(token)
        }
    }

    private fun startBackRoom(lifecycleScope: CoroutineScope, token : String){
        backStreamingStatus = "BackStreamingStarted"
        Log.v(errorTag,"startBackRoom")
        lifecycleScope.launch {
            connectToRoomForBack(token)
        }
    }
    private suspend fun connectToRoomForFront(token : String) {
        try {
            Log.v(errorTag,"Connecting to front room...  $token")
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
            localParticipant.name = "FrontSender2"
            showFrontCameraView(localParticipant)
            frontStreamingStatus = "Started"
            Log.v(errorTag,"frontStreamingStatus Started ${frontRoom.name}")
        } catch (e: Throwable) {
            Log.v(errorTag,"connectToRoomForFront error ${e.message}")
            frontStreamingStatus = e.message ?: "Error"
            e.printStackTrace()
        }
    }

    private suspend fun connectToRoomForBack(token : String) {
        try {
            Log.v(errorTag,"Connecting to back room...  $token")
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
            localParticipant.name = "BackSender2"
            showBackCameraView(localParticipant)
            backStreamingStatus = "Started"
            Log.v(errorTag,"backStreamingStatus Started ${backRoom.name}")
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
        frontRoom.disconnect()
        backRoom.disconnect()
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