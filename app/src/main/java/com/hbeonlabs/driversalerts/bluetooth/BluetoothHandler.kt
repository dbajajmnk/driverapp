package com.hbeonlab.rms.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.hbeonlabs.driversalerts.bluetooth.BluetoothUtills
import java.io.IOException
import java.util.*

class BluetoothHandler(val activity: Activity, val handler: Handler) {
    private val TAG: String = "BluetoothHandler"
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val NAME: String = "RMS Bluetooth"
    private val REQUEST_ENABLE_BT: Int = 100
    var bluetoothAdapter: BluetoothAdapter? = null
    var bluetoothPermissionLauncher: ActivityResultLauncher<String>? = null
    lateinit var connectThread : ConnectThread
    init {
        var permissionMissing = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        }else{
            false
        }
        if(permissionMissing){
            bluetoothPermissionLauncher = (activity as ComponentActivity).registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted: Boolean? ->
                BluetoothUtills.onPermissionsResult(activity, granted!!) { initBluetoothAdapter() }
            }
            if (BluetoothUtills.hasPermissions(activity,bluetoothPermissionLauncher))
                initBluetoothAdapter()
        }else {
            initBluetoothAdapter()
        }

        //val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        //activity.registerReceiver(receiver, filter)
    }

    private fun initBluetoothAdapter(){
        val bluetoothManager: BluetoothManager =
            activity.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            handler.sendEmptyMessage(1)
            // Device doesn't support Bluetooth
        } else {
            enableBluetooth()
        }
    }
    private fun enableBluetooth(){
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    @SuppressLint("MissingPermission")
    fun queryDevices():ArrayList<BluetoothDevice>{
        val listItems = ArrayList<BluetoothDevice>()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            if (device.type != BluetoothDevice.DEVICE_TYPE_LE)
                listItems.add(device)
        }

        return listItems
    }

    fun makeDeviceDiscoverable(){
        val requestCode = 1;
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        activity.startActivityForResult(discoverableIntent, requestCode)
    }

    @SuppressLint("MissingPermission")
    fun startConnection(deviceAddress: String) : String?{
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        device?.let {
            connectThread = ConnectThread(it)
            connectThread.start() }
        return device?.name
    }

    fun getDevice(deviceAddress: String) : BluetoothDevice?{
        return bluetoothAdapter?.getRemoteDevice(deviceAddress)
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    manageMyConnectedSocket(it)
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }
        }

        private fun manageMyConnectedSocket(bluetoothSocket: BluetoothSocket) {
            //TODO("Not yet implemented")
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            //bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                try {
                    socket.connect()
                }catch (e:Exception){
                    e.printStackTrace()
                }
                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket)
            }
        }

        private fun manageMyConnectedSocket(socket: BluetoothSocket) {
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                }
            }
        }
    }

    fun onDestroy() {
        //activity.unregisterReceiver(receiver)
    }
}