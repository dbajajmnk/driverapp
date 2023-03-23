package com.hbeonlabs.driversalerts.webrtc;

import static org.webrtc.SessionDescription.Type.ANSWER;
import static org.webrtc.SessionDescription.Type.OFFER;
import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_DISCONNECT;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.EglRenderer;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;

public class WebRtcHelper {
    private Context context;
    private static final String TAG = "CompleteActivity";
    private static final int RC_CALL = 111;
    public static final String VIDEO_TRACK_ID = "ARDAMSv0";
    public static final int VIDEO_RESOLUTION_WIDTH = 1280;
    public static final int VIDEO_RESOLUTION_HEIGHT = 720;
    public static final int FPS = 30;

    private Socket socket;
    private boolean isInitiator;
    private boolean isChannelReady;
    private boolean isStarted;


    MediaConstraints audioConstraints;
    MediaConstraints videoConstraints;
    MediaConstraints sdpConstraints;
    VideoSource videoSource;
    VideoTrack localVideoTrack;
    AudioSource audioSource;
    AudioTrack localAudioTrack;
    SurfaceTextureHelper surfaceTextureHelper;

    VideoRenderer frontStreamingRenderer;
    VideoRenderer backStreamingRenderer;
    MediaStream frontCameraMediaStream, backCameraMediaStream;
    private PeerConnection frontStreamingPeerConnection;
    private PeerConnection backStreamingPeerConnection;
    private EglBase rootEglBase;
    private PeerConnectionFactory factory;
    private VideoTrack videoTrackFrontCamera;
    private VideoTrack videoTrackBackCamera;

    private SurfaceViewRenderer senderSurfaceViewRenderer;
    private SurfaceViewRenderer backCameraSVR;
    private SurfaceViewRenderer receiverFrontSVR;
    private SurfaceViewRenderer receiverBackSVR;
    private static WebRtcHelper webRtcHelperInstance;
    private WebRtcHelper(){}
    public static WebRtcHelper getInstance(){
        if(webRtcHelperInstance == null){
            webRtcHelperInstance = new WebRtcHelper();
        }
        return webRtcHelperInstance;
    }
    public void onDestroy() {
        if (socket != null) {
            sendMessage("bye");
            socket.disconnect();
        }
        stopStreamingVideo();
    }

    /*public void start(Context context, SurfaceViewRenderer senderSurfaceViewRenderer, SurfaceViewRenderer receiverSurfaceViewRenderer) {
        this.context = context;
        this.senderSurfaceViewRenderer = senderSurfaceViewRenderer;
        this.receiverSurfaceViewRenderer = receiverSurfaceViewRenderer;

        connectToSignallingServer();

        initializeSurfaceViews();

        initializePeerConnectionFactory();

        if(senderSurfaceViewRenderer != null) {
            showFrontCameraVideoTrack();
        }

        initializePeerConnections();

        if(senderSurfaceViewRenderer != null) {
            startFrontStreamingVideo();
        }
    }*/

    public void init(Context context) {
        this.context = context;
        connectToSignallingServer();
        initializePeerConnectionFactory();
        initializePeerConnections();
    }

    public void startFrontStreaming(SurfaceViewRenderer senderSurfaceViewRenderer) {
        this.senderSurfaceViewRenderer = senderSurfaceViewRenderer;
        initializeSurfaceViews(this.senderSurfaceViewRenderer);
        showFrontCameraVideoTrack();
        startFrontStreamingVideo();
    }

    public void startBackStreaming(SurfaceViewRenderer backSurfaceViewRenderer) {
        this.backCameraSVR = backSurfaceViewRenderer;
        initializeSurfaceViews(this.backCameraSVR);
        showBackCameraVideoTrack();
        startBackStreamingVideo();
    }

    public void startReceiverFrontStreaming(SurfaceViewRenderer receiverSurfaceViewRenderer) {
        this.receiverFrontSVR = receiverSurfaceViewRenderer;
        initializeSurfaceViews(this.receiverFrontSVR);
    }


    public void addFrameListener(EglRenderer.FrameListener listener){
        senderSurfaceViewRenderer.addFrameListener(listener,1.0f);
    }

    private void connectToSignallingServer() {
        try {
            // For me this was "http://192.168.1.220:3000";
            // $ hostname -I
            //String URL = "https://calm-badlands-59575.herokuapp.com/";
            //String URL = "http://68.178.160.179:5000/";
            String URL = "http://68.178.160.179:3030";
            Log.e(TAG, "REPLACE ME: IO Socket:" + URL);
            socket = IO.socket(URL);
            this.socket.connect();
            socket.on(EVENT_CONNECT, args -> {
                Log.d(TAG, "connectToSignallingServer: connect");
                socket.emit("create or join", "foo");
            }).on("ipaddr", args -> {
                Log.d(TAG, "connectToSignallingServer: ipaddr");
            }).on("created", args -> {
                Log.d(TAG, "connectToSignallingServer: created");
                isInitiator = true;
            }).on("full", args -> {
                Log.d(TAG, "connectToSignallingServer: full");
            }).on("join", args -> {
                Log.d(TAG, "connectToSignallingServer: join");
                Log.d(TAG, "connectToSignallingServer: Another peer made a request to join room");
                Log.d(TAG, "connectToSignallingServer: This peer is the initiator of room");
                isChannelReady = true;
            }).on("joined", args -> {
                Log.d(TAG, "connectToSignallingServer: joined");
                isChannelReady = true;
            }).on("log", args -> {
                for (Object arg : args) {
                    Log.d(TAG, "connectToSignallingServer: " + String.valueOf(arg));
                }
            }).on("message", args -> {
                Log.d(TAG, "connectToSignallingServer: got a message");
            }).on("message", args -> {
                try {
                    if (args[0] instanceof String) {
                        String message = (String) args[0];
                        if (message.equals("got user media")) {
                            maybeStart();
                        }
                    } else {
                        JSONObject message = (JSONObject) args[0];
                        Log.d(TAG, "connectToSignallingServer: got message " + message);
                        if (message.getString("type").equals("offer")) {
                            Log.d(TAG, "connectToSignallingServer: received an offer " + isInitiator + " " + isStarted);
                            if (!isInitiator && !isStarted) {
                                maybeStart();
                            }
                            frontStreamingPeerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, message.getString("sdp")));
                            doAnswer();
                        } else if (message.getString("type").equals("answer") && isStarted) {
                            frontStreamingPeerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, message.getString("sdp")));
                        } else if (message.getString("type").equals("candidate") && isStarted) {
                            Log.d(TAG, "connectToSignallingServer: receiving candidates");
                            IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                            frontStreamingPeerConnection.addIceCandidate(candidate);
                        }
                        /*else if (message === 'bye' && isStarted) {
                        handleRemoteHangup();
                    }*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).on(EVENT_DISCONNECT, args -> {
                Log.d(TAG, "connectToSignallingServer: disconnect");
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
//MirtDPM4
    private void doAnswer() {
        frontStreamingPeerConnection.createAnswer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                frontStreamingPeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                JSONObject message = new JSONObject();
                try {
                    message.put("type", "answer");
                    message.put("sdp", sessionDescription.description);
                    sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new MediaConstraints());
    }

    private void maybeStart() {
        Log.d(TAG, "maybeStart: " + isStarted + " " + isChannelReady);
        if (!isStarted && isChannelReady) {
            isStarted = true;
            if (isInitiator) {
                doCall();
            }
        }
    }

    private void doCall() {
        MediaConstraints sdpMediaConstraints = new MediaConstraints();

        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        frontStreamingPeerConnection.createOffer(new SimpleSdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                Log.d(TAG, "onCreateSuccess: ");
                frontStreamingPeerConnection.setLocalDescription(new SimpleSdpObserver(), sessionDescription);
                JSONObject message = new JSONObject();
                try {
                    message.put("type", "offer");
                    message.put("sdp", sessionDescription.description);
                    sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, sdpMediaConstraints);
    }

    private void sendMessage(Object message) {
        socket.emit("message", message);
    }

    private void initializeSurfaceViews(SurfaceViewRenderer surfaceViewRenderer) {
        surfaceViewRenderer.init(rootEglBase.getEglBaseContext(), null);
        surfaceViewRenderer.setEnableHardwareScaler(true);
        surfaceViewRenderer.setMirror(true);
    }

    private void initializePeerConnectionFactory() {
        rootEglBase = EglBase.create();
        PeerConnectionFactory.initializeAndroidGlobals(context, true, true, true);
        factory = new PeerConnectionFactory(null);
        factory.setVideoHwAccelerationOptions(rootEglBase.getEglBaseContext(), rootEglBase.getEglBaseContext());
    }

    private void showFrontCameraVideoTrack() {
        audioConstraints = new MediaConstraints();
        VideoCapturer videoCapturer = createVideoCapturer();
        VideoSource videoSource = factory.createVideoSource(videoCapturer);
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackFrontCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrackFrontCamera.setEnabled(true);
        videoTrackFrontCamera.addRenderer(new VideoRenderer(senderSurfaceViewRenderer));

        //create an AudioSource instance
        audioSource = factory.createAudioSource(audioConstraints);
        localAudioTrack = factory.createAudioTrack("101", audioSource);
    }

    private void showBackCameraVideoTrack() {
        VideoCapturer videoCapturer = createBackCaptor(new Camera1Enumerator(true));
        VideoSource videoSource = factory.createVideoSource(videoCapturer);
        videoCapturer.startCapture(VIDEO_RESOLUTION_WIDTH, VIDEO_RESOLUTION_HEIGHT, FPS);

        videoTrackBackCamera = factory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
        videoTrackBackCamera.setEnabled(true);
        videoTrackBackCamera.addRenderer(new VideoRenderer(backCameraSVR));
    }

    private void initializePeerConnections() {
        frontStreamingPeerConnection = createPeerConnectionForFront(factory);
        backStreamingPeerConnection = createPeerConnectionForBack(factory);
    }

    private void startFrontStreamingVideo() {
        Log.d("CompleteActivity", "startStreamingVideo ");
        frontCameraMediaStream = factory.createLocalMediaStream("ARDAMS");
        frontCameraMediaStream.addTrack(videoTrackFrontCamera);
        frontCameraMediaStream.addTrack(localAudioTrack);
        frontStreamingPeerConnection.addStream(frontCameraMediaStream);

        sendMessage("got user media");
    }

    private void startBackStreamingVideo() {
        Log.d("CompleteActivity", "startStreamingVideo ");
        backCameraMediaStream = factory.createLocalMediaStream("ARDAMS");
        backCameraMediaStream.addTrack(videoTrackBackCamera);
        backCameraMediaStream.addTrack(localAudioTrack);
        backStreamingPeerConnection.addStream(backCameraMediaStream);

        sendMessage("got user media");
    }

    private void stopStreamingVideo() {
        if(frontCameraMediaStream != null) {
            frontStreamingPeerConnection.removeStream(frontCameraMediaStream);
            sendMessage("stopStreamingVideo");
        }
        if(backCameraMediaStream != null) {
            backStreamingPeerConnection.removeStream(backCameraMediaStream);
        }
    }

    private PeerConnection createPeerConnectionForFront(PeerConnectionFactory factory) {
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        String URL = "stun:stun.l.google.com:19302";
        iceServers.add(new PeerConnection.IceServer(URL));

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        MediaConstraints pcConstraints = new MediaConstraints();

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: ");
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(TAG, "onIceCandidate: ");
                JSONObject message = new JSONObject();

                try {
                    message.put("type", "candidate");
                    message.put("label", iceCandidate.sdpMLineIndex);
                    message.put("id", iceCandidate.sdpMid);
                    message.put("candidate", iceCandidate.sdp);

                    Log.d(TAG, "onIceCandidate: sending candidate " + message);
                    sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream: " + mediaStream.videoTracks.size());
                if(receiverFrontSVR != null) {
                    VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                    AudioTrack remoteAudioTrack = mediaStream.audioTracks.get(0);
                    remoteAudioTrack.setEnabled(true);
                    remoteVideoTrack.setEnabled(true);
                    frontStreamingRenderer = new VideoRenderer(receiverFrontSVR);
                    remoteVideoTrack.addRenderer(frontStreamingRenderer);
                }

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                AudioTrack remoteAudioTrack = mediaStream.audioTracks.get(0);
                remoteAudioTrack.setEnabled(false);
                remoteVideoTrack.setEnabled(false);
                remoteVideoTrack.removeRenderer(frontStreamingRenderer);
                remoteVideoTrack.dispose();
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }
        };

        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    private PeerConnection createPeerConnectionForBack(PeerConnectionFactory factory) {
        ArrayList<PeerConnection.IceServer> iceServers = new ArrayList<>();
        String URL = "stun:stun.l.google.com:19302";
        iceServers.add(new PeerConnection.IceServer(URL));

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        MediaConstraints pcConstraints = new MediaConstraints();

        PeerConnection.Observer pcObserver = new PeerConnection.Observer() {
            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
                Log.d(TAG, "onSignalingChange: ");
            }

            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "onIceConnectionChange: ");
            }

            @Override
            public void onIceConnectionReceivingChange(boolean b) {
                Log.d(TAG, "onIceConnectionReceivingChange: ");
            }

            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
                Log.d(TAG, "onIceGatheringChange: ");
            }

            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                Log.d(TAG, "onIceCandidate: ");
                JSONObject message = new JSONObject();

                try {
                    message.put("type", "candidate");
                    message.put("label", iceCandidate.sdpMLineIndex);
                    message.put("id", iceCandidate.sdpMid);
                    message.put("candidate", iceCandidate.sdp);

                    Log.d(TAG, "onIceCandidate: sending candidate " + message);
                    sendMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
                Log.d(TAG, "onIceCandidatesRemoved: ");
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                Log.d(TAG, "onAddStream: " + mediaStream.videoTracks.size());
                if(receiverFrontSVR != null) {
                    VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                    AudioTrack remoteAudioTrack = mediaStream.audioTracks.get(0);
                    remoteAudioTrack.setEnabled(true);
                    remoteVideoTrack.setEnabled(true);
                    backStreamingRenderer = new VideoRenderer(receiverBackSVR);
                    remoteVideoTrack.addRenderer(backStreamingRenderer);
                }

            }

            @Override
            public void onRemoveStream(MediaStream mediaStream) {
                Log.d(TAG, "onRemoveStream: ");
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                AudioTrack remoteAudioTrack = mediaStream.audioTracks.get(0);
                remoteAudioTrack.setEnabled(false);
                remoteVideoTrack.setEnabled(false);
                remoteVideoTrack.removeRenderer(backStreamingRenderer);
                remoteVideoTrack.dispose();
            }

            @Override
            public void onDataChannel(DataChannel dataChannel) {
                Log.d(TAG, "onDataChannel: ");
            }

            @Override
            public void onRenegotiationNeeded() {
                Log.d(TAG, "onRenegotiationNeeded: ");
            }
        };

        return factory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer;
        if (useCamera2()) {
            videoCapturer = createCameraCapturer(new Camera2Enumerator(context));
        } else {
            videoCapturer = createCameraCapturer(new Camera1Enumerator(true));
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(context);
    }


    private VideoCapturer createBackCaptor(CameraEnumerator enumerator){
        String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find back facing camera
        for (String deviceName : deviceNames) {
            if (enumerator.isBackFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // back facing camera not found, try something else
        for (String deviceName : deviceNames) {
            if (!enumerator.isBackFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        return null;
    }

}
