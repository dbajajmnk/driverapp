package com.hbeonlabs.driversalerts.bluetooth

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import com.hbeonlab.rms.bluetooth.Contstants

class BluetoothDevicesActivity : ComponentActivity() {

    private val listItems : MutableLiveData<MutableList<BluetoothDevice>> = MutableLiveData(mutableListOf())
    private var hasPermissions = false
    private var showProgress = false
    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                Contstants.STATE_CONNECTING -> {
                    if(message.data.getBoolean(Contstants.EXTRA_IS_CLIENT_DEVICE)) {
                        Toast.makeText(
                            this@BluetoothDevicesActivity,
                            "Connecting...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                Contstants.STATE_CONNECTED -> {
                    showProgress = false
                    Toast.makeText(this@BluetoothDevicesActivity, "Connected", Toast.LENGTH_SHORT)
                        .show()
                    goToChat()
                }
                Contstants.STATE_CONNECTION_FAILED -> {
                    showProgress = false
                    Toast.makeText(this@BluetoothDevicesActivity,"Connection Failed!", Toast.LENGTH_SHORT).show()
                }
                Contstants.STATE_MESSAGE_RECEIVED -> {
                    val readBuff = message.obj as ByteArray
                    val tempMsg = String(readBuff, 0, message.arg1)
                    Toast.makeText(this@BluetoothDevicesActivity,"Message received : $tempMsg", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkBluetoothPermisssions()

        connectToBluetooth()
    }

    private fun checkBluetoothPermisssions() {
        val requestBluetoothPermissionLauncherForRefresh = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted: Boolean? ->
            BluetoothUtills.onPermissionsResult(this, granted!!) {
                hasPermissions = true
                connectToBluetooth()
            }
        }
        hasPermissions = BluetoothUtills.hasPermissions(this,requestBluetoothPermissionLauncherForRefresh)
        if(hasPermissions){
            connectToBluetooth()
        }
    }

    private fun connectToBluetooth() {
        if(hasPermissions){
            BluetoothUtills.initBluetoothAdapter(this, mHandler, resultLauncher)
            val deviceList = BluetoothUtills.queryDevices()
            listItems.value = deviceList
            BluetoothUtills.startServerThread(mHandler)
            if(deviceList.isNotEmpty())
                Toast.makeText(this, "Select bluetooth device to connect.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun connectToDevice(bluetoothDevice: BluetoothDevice) {
        showProgress = true
        BluetoothUtills.startConnection(bluetoothDevice)
    }

    private fun goToChat(){
        showProgress = false

    }

    override fun onDestroy() {
        super.onDestroy()
        BluetoothUtills.disconnect()
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            connectToBluetooth()
        }
    }

}