package com.hbeonlabs.driversalerts.utils

import android.content.Context
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection.*
import org.webrtc.SessionDescription.Type.ANSWER
import org.webrtc.SessionDescription.Type.OFFER
import java.net.URISyntaxException

class LiveStreamingHelper {

    private var socket: Socket
    private var isInitiator = false
    private var isChannelReady = false
    private var isStarted = false
    private val TAG = "LiveStreamingHelper"
    private var peerConnection: PeerConnection? = null
    private val rootEglBase: EglBase? = null
    private var factory: PeerConnectionFactory? = null
    private var videoTrackFromCamera: VideoTrack? = null

    var audioConstraints: MediaConstraints? = null
    var audioSource: AudioSource? = null
    var localAudioTrack: AudioTrack? = null

    val VIDEO_TRACK_ID = "ARDAMSv0"
    val VIDEO_RESOLUTION_WIDTH = 1280
    val VIDEO_RESOLUTION_HEIGHT = 720
    val FPS = 30
    lateinit var context : Context
    init {
        val URL = "http://68.178.160.179:3030"
        Log.e(TAG,"Live streaming url : $URL")
        socket = IO.socket(URL)
    }
    fun start(context : Context){
        this.context = context

        connectToSignallingServer()

        initializePeerConnectionFactory()

        createVideoTrackFromCameraAndShowIt()

        initializePeerConnections()

        startStreamingVideo()
    }
    private fun connectToSignallingServer() {
        try {
            // For me this was "http://192.168.1.220:3000";
            // $ hostname -I
            //String URL = "https://calm-badlands-59575.herokuapp.com/";
            //String URL = "http://68.178.160.179:5000/";
            val URL = "http://68.178.160.179:3030"
            Log.e(TAG,"REPLACE ME: IO Socket:$URL")
            socket = IO.socket(URL)
            socket.on(Socket.EVENT_CONNECT, Emitter.Listener { args: Array<Any?>? ->
                Log.d(TAG,"connectToSignallingServer: connect")
                socket.emit("create or join", "foo")
            }).on("ipaddr",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d(TAG,"connectToSignallingServer: ipaddr")
                }).on("created",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d(TAG,"connectToSignallingServer: created")
                    isInitiator = true
                }).on("full",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d(TAG,"connectToSignallingServer: full")
                }).on("join",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d( TAG,"connectToSignallingServer: join")
                    Log.d(TAG,"connectToSignallingServer: Another peer made a request to join room")
                    Log.d(TAG,"connectToSignallingServer: This peer is the initiator of room")
                    isChannelReady = true
                }).on("joined", Emitter.Listener { args: Array<Any?>? ->
                Log.d(TAG,"connectToSignallingServer: joined")
                isChannelReady = true
            }).on("log",
                Emitter.Listener { args: Array<Any> ->
                    for (arg in args) {
                        Log.d(TAG,"connectToSignallingServer: $arg")
                    }
                }).on("message",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d(TAG,"connectToSignallingServer: got a message")
                }).on("message",
                Emitter.Listener { args: Array<Any> ->
                    try {
                        if (args[0] is String) {
                            val message = args[0] as String
                            if (message == "got user media") {
                                //maybeStart()
                            }
                        } else {
                            val message = args[0] as JSONObject
                            Log.d(TAG,"connectToSignallingServer: got message $message")
                            if (message.getString("type") == "offer") {
                                Log.d(TAG,"connectToSignallingServer: received an offer $isInitiator $isStarted")
                                if (!isInitiator && !isStarted) {
                                    maybeStart()
                                }
                                peerConnection?.setRemoteDescription(
                                    SimpleSdpObserver(),
                                    SessionDescription(OFFER, message.getString("sdp"))
                                )
                                doAnswer()
                            } else if (message.getString("type") == "answer" && isStarted) {
                                Log.d(TAG,"message type: answer")
                                peerConnection?.setRemoteDescription(
                                    SimpleSdpObserver(),
                                    SessionDescription(ANSWER, message.getString("sdp"))
                                )
                            } else if (message.getString("type") == "candidate" && isStarted) {
                                Log.d(TAG,"connectToSignallingServer: receiving candidates")
                                val candidate = IceCandidate(
                                    message.getString("id"),
                                    message.getInt("label"),
                                    message.getString("candidate")
                                )
                                peerConnection?.addIceCandidate(candidate)
                            }
                            /*else if (message === 'bye' && isStarted) {
            handleRemoteHangup();
        }*/
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }).on(
                Socket.EVENT_DISCONNECT,
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d(TAG,"connectToSignallingServer: disconnect")
                })
            socket.connect()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun doAnswer() {
        peerConnection?.createAnswer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                peerConnection?.setLocalDescription(SimpleSdpObserver(), sessionDescription)
                val message = JSONObject()
                try {
                    message.put("type", "answer")
                    message.put("sdp", sessionDescription.description)
                    sendMessage(message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, MediaConstraints())
    }

    private fun maybeStart() {
        Log.d(TAG,"maybeStart: $isStarted $isChannelReady")
        if (!isStarted && isChannelReady) {
            isStarted = true
            if (isInitiator) {
                doCall()
            }
        }
    }

    private fun doCall() {
        val sdpMediaConstraints = MediaConstraints()
        sdpMediaConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpMediaConstraints.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
        peerConnection?.createOffer(object : SimpleSdpObserver() {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                Log.d(TAG, "onCreateSuccess: ")
                peerConnection?.setLocalDescription(SimpleSdpObserver(), sessionDescription)
                val message = JSONObject()
                try {
                    message.put("type", "offer")
                    message.put("sdp", sessionDescription.description)
                    sendMessage(message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, sdpMediaConstraints)
    }

    private fun sendMessage(message: Any) {
        socket.emit("message", message)
    }


    private fun initializePeerConnectionFactory() {
        PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true)
        factory = PeerConnectionFactory(null)
        factory?.setVideoHwAccelerationOptions(
            rootEglBase?.getEglBaseContext(),
            rootEglBase?.getEglBaseContext()
        )
    }

    private fun createVideoTrackFromCameraAndShowIt() {
        audioConstraints = MediaConstraints()
        val videoCapturer = createVideoCapturer()
        val videoSource: VideoSource? = factory?.createVideoSource(videoCapturer)
        videoCapturer?.startCapture(VIDEO_RESOLUTION_WIDTH,VIDEO_RESOLUTION_HEIGHT,FPS)
        videoTrackFromCamera = factory?.createVideoTrack(VIDEO_TRACK_ID,videoSource)
        videoTrackFromCamera?.setEnabled(true)
        //videoTrackFromCamera?.addRenderer(VideoRenderer(binding.surfaceView))     TODO Add front facing camera

        //create an AudioSource instance
        audioSource = factory?.createAudioSource(audioConstraints)
        localAudioTrack = factory?.createAudioTrack("101", audioSource)
    }

    private fun initializePeerConnections() {
        peerConnection = factory?.let { createPeerConnection(it) }
    }

    private fun startStreamingVideo() {
        val mediaStream: MediaStream? = factory?.createLocalMediaStream("ARDAMS")
        mediaStream?.addTrack(videoTrackFromCamera)
        mediaStream?.addTrack(localAudioTrack)
        peerConnection?.addStream(mediaStream)
        sendMessage("got user media")
    }

    private fun createPeerConnection(factory: PeerConnectionFactory): PeerConnection {
        val iceServers = ArrayList<IceServer>()
        val URL = "stun:stun.l.google.com:19302"
        iceServers.add(IceServer(URL))
        val rtcConfig = RTCConfiguration(iceServers)
        val pcConstraints = MediaConstraints()
        val pcObserver: Observer = object : Observer {
            override fun onSignalingChange(signalingState: SignalingState) {
                Log.d(TAG, "onSignalingChange: ")
            }

            override fun onIceConnectionChange(iceConnectionState: IceConnectionState) {
                Log.d(TAG,"onIceConnectionChange: ")
            }

            override fun onIceConnectionReceivingChange(b: Boolean) {
                Log.d(TAG,"onIceConnectionReceivingChange: ")
            }

            override fun onIceGatheringChange(iceGatheringState: IceGatheringState) {
                Log.d(TAG,"onIceGatheringChange: ")
            }

            override fun onIceCandidate(iceCandidate: IceCandidate) {
                Log.d(TAG, "onIceCandidate: ")
                val message = JSONObject()
                try {
                    message.put("type", "candidate")
                    message.put("label", iceCandidate.sdpMLineIndex)
                    message.put("id", iceCandidate.sdpMid)
                    message.put("candidate", iceCandidate.sdp)
                    Log.d(TAG,"onIceCandidate: sending candidate $message")
                    sendMessage(message)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onIceCandidatesRemoved(iceCandidates: Array<IceCandidate>) {
                Log.d(TAG,"onIceCandidatesRemoved: ")
            }

            override fun onAddStream(mediaStream: MediaStream) {
                Log.d(TAG,"onAddStream: " + mediaStream.videoTracks.size)
                val remoteVideoTrack = mediaStream.videoTracks[0]
                val remoteAudioTrack = mediaStream.audioTracks[0]
                remoteAudioTrack.setEnabled(true)
                remoteVideoTrack.setEnabled(true)
                //remoteVideoTrack.addRenderer(VideoRenderer(binding.surfaceView2))     TODO Add stream to surface view for peer camera
            }

            override fun onRemoveStream(mediaStream: MediaStream) {
                Log.d(TAG, "onRemoveStream: ")
            }

            override fun onDataChannel(dataChannel: DataChannel) {
                Log.d(TAG, "onDataChannel: ")
            }

            override fun onRenegotiationNeeded() {
                Log.d(TAG,"onRenegotiationNeeded: ")
            }
        }
        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver)
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