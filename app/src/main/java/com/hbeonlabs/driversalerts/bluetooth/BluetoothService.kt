package com.hbeonlab.rms.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.os.Handler
import android.os.Message
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class BluetoothService(adapter: BluetoothAdapter) {

    private val bluetoothAdapter : BluetoothAdapter = adapter
    private lateinit var handler: Handler
    private lateinit var  sendReceive : SendReceive

    fun setHandler(handler: Handler){
        this.handler = handler
    }

    fun write(string: String) {
        sendReceive.write(string.toByteArray())
    }

    fun disconnect(){
        if(this::sendReceive.isInitialized)
            sendReceive.closeSocket()
    }
    @SuppressLint("MissingPermission")
    inner class ServerClass : Thread() {
        private var serverSocket: BluetoothServerSocket? = null
        override fun run() {
            var socket: BluetoothSocket? = null
            var continueListening = true
            while (socket == null && continueListening) {
                try {
                    val message = Message.obtain()
                    message.what = Contstants.STATE_CONNECTING
                    message.data = Bundle().also { it.putBoolean(Contstants.EXTRA_IS_CLIENT_DEVICE, false) }
                    handler.sendMessage(message)
                    socket = serverSocket!!.accept()
                } catch (e: IOException) {
                    e.printStackTrace()
                    continueListening = false
                    handler.sendMessage(Message.obtain().also { it?.what = Contstants.STATE_CONNECTION_FAILED })
                }
                if (socket != null) {
                    handler.sendMessage(Message.obtain().also { it?.what = Contstants.STATE_CONNECTED })
                    sendReceive = SendReceive(socket)
                    sendReceive.start()
                    closeServerSocket()
                    break
                }
            }
        }

        init {
            try {
                serverSocket =
                    bluetoothAdapter.listenUsingRfcommWithServiceRecord("Bluetooth Chat App", UUID.fromString(Contstants.UUID))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun closeServerSocket(){
            serverSocket?.close()
            serverSocket = null
        }
    }

    @SuppressLint("MissingPermission")
    inner class ClientClass(device: BluetoothDevice) : Thread() {
        private var socket: BluetoothSocket? = null
        override fun run() {
            try {
                val messageConnecting = Message.obtain()
                messageConnecting.what = Contstants.STATE_CONNECTING
                messageConnecting.data = Bundle().also { it.putBoolean(Contstants.EXTRA_IS_CLIENT_DEVICE, true) }
                handler.sendMessage(messageConnecting)
                socket!!.connect()
                handler.sendMessage(Message.obtain().also { it?.what = Contstants.STATE_CONNECTED })
                sendReceive = SendReceive(socket)
                sendReceive.start()
            } catch (e: IOException) {
                e.printStackTrace()
                handler.sendMessage(Message.obtain().also { it?.what = Contstants.STATE_CONNECTION_FAILED })
            }
        }

        init {
            try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(Contstants.UUID))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    inner class SendReceive(val bluetoothSocket: BluetoothSocket?) : Thread() {
        private val inputStream: InputStream?
        private val outputStream: OutputStream?
        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int
            var continueReading = true
            while (continueReading) {
                try {
                    bytes = inputStream?.read(buffer)!!
                    handler.obtainMessage(Contstants.STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget()
                } catch (e: IOException) {
                    continueReading = false
                    e.printStackTrace()
                    handler.sendMessage(Message.obtain().also { it?.what = Contstants.STATE_DISCONNECTED })
                    closeSocket()
                }
            }
        }

        fun write(bytes: ByteArray?) {
            try {
                outputStream?.write(bytes)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        init {
            var tempIn: InputStream? = null
            var tempOut: OutputStream? = null
            try {
                tempIn = bluetoothSocket!!.inputStream
                tempOut = bluetoothSocket.outputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
            inputStream = tempIn
            outputStream = tempOut
        }

        fun closeSocket(){
            bluetoothSocket?.close()
        }
    }
}