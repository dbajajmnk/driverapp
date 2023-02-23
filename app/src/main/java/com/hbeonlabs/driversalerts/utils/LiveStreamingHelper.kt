package com.hbeonlabs.driversalerts.utils

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.PeerConnection
import org.webrtc.SessionDescription
import java.net.URISyntaxException

class LiveStreamingHelper {

    private var socket: Socket
    private var isInitiator = false
    private var isChannelReady = false
    private var isStarted = false
    private val TAG = "LiveStreamingHelper"
    private val peerConnection: PeerConnection? = null
    init {
        val URL = "http://68.178.160.179:3030"
        Log.e(TAG,"Live streaming url : $URL")
        socket = IO.socket(URL)
    }
    fun start(){
        connectToSignallingServer()
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
                                /*if (!isInitiator && !isStarted) {
                                    maybeStart()
                                }
                                peerConnection.setRemoteDescription(
                                    SimpleSdpObserver(),
                                    SessionDescription(OFFER, message.getString("sdp"))
                                )
                                doAnswer()*/
                            } else if (message.getString("type") == "answer" && isStarted) {
                                Log.d(TAG,"message type: answer")
                                /*peerConnection.setRemoteDescription(
                                    SimpleSdpObserver(),
                                    SessionDescription(ANSWER, message.getString("sdp"))
                                )*/
                            } else if (message.getString("type") == "candidate" && isStarted) {
                                Log.d(TAG,"connectToSignallingServer: receiving candidates")
                                /*val candidate = IceCandidate(
                                    message.getString("id"),
                                    message.getInt("label"),
                                    message.getString("candidate")
                                )
                                peerConnection.addIceCandidate(candidate)*/
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
}