package com.hbeonlabs.driversalerts.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.ACTION_STATE_CHANGED
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import com.hbeonlab.rms.bluetooth.BluetoothService
import java.util.*

object BluetoothUtills {

    var bluetoothAdapter: BluetoothAdapter? = null
    var bluetoothService: BluetoothService?= null
    var serverThread : BluetoothService.ServerClass? = null
    interface PermissionGrantedCallback {
        fun call()
    }

    /**
     * sort by name, then address. sort named devices first
     */
    @SuppressLint("MissingPermission")
    fun compareTo(a: BluetoothDevice, b: BluetoothDevice): Int {
        val aValid = a.name != null && !a.name.isEmpty()
        val bValid = b.name != null && !b.name.isEmpty()
        if (aValid && bValid) {
            val ret = a.name.compareTo(b.name)
            return if (ret != 0) ret else a.address.compareTo(b.address)
        }
        if (aValid) return -1
        return if (bValid) +1 else a.address.compareTo(b.address)
    }

    /**
     * Android 12 permission handling
     */
    private fun showRationaleDialog(fragment: Activity, listener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(fragment)
//        builder.setTitle(fragment.getString(R.string.bluetooth_permission_title))
//        builder.setMessage(fragment.getString(R.string.bluetooth_permission_grant))
        builder.setNegativeButton("Cancel", null)
        builder.setPositiveButton("Continue", listener)
        builder.show()
    }

    private fun showSettingsDialog(activity: Activity) {
        val s = activity.resources.getString(
            activity.resources.getIdentifier(
                "@android:string/permgrouplab_nearby_devices",
                null,
                null
            )
        )
        val builder = AlertDialog.Builder(activity)
//        builder.setTitle(activity.getString(R.string.bluetooth_permission_title))
//        builder.setMessage(
//            String.format(
//                activity.getString(R.string.bluetooth_permission_denied),
//                s
//            )
//        )
        builder.setNegativeButton("Cancel", null)
        builder.setPositiveButton(
            "Settings"
        ) { dialog: DialogInterface?, which: Int ->
//            activity.startActivity(
//                Intent(
//                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                    Uri.parse("package:" + BuildConfig.APPLICATION_ID)
//                )
//            )
        }
        builder.show()
    }

    fun hasPermissions(
        activity: Activity,
        requestPermissionLauncher: ActivityResultLauncher<String>?
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val missingPermissions =
            activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        val showRationale =
            activity.shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)
        return if (missingPermissions) {
            if (showRationale) {
                showRationaleDialog(
                    activity
                ) { dialog: DialogInterface?, which: Int ->
                    requestPermissionLauncher?.launch(
                        Manifest.permission.BLUETOOTH_CONNECT
                    )
                }
            } else {
                requestPermissionLauncher?.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
            false
        } else {
            true
        }
    }

    fun onPermissionsResult(activity: Activity, granted: Boolean, cb: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
        val showRationale =
            activity.shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)
        if (granted) {
            cb()
        } else if (showRationale) {
            showRationaleDialog(
                activity
            ) { _: DialogInterface?, _: Int -> cb() }
        } else {
            showSettingsDialog(activity)
        }
    }

    fun initBluetoothAdapter(activity: Activity, handler: Handler, resultLauncher: ActivityResultLauncher<Intent>){
        val bluetoothManager: BluetoothManager =
            activity.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            handler.sendEmptyMessage(1)
            // Device doesn't support Bluetooth
        } else {
            enableBluetooth(resultLauncher)
        }
    }
    private fun enableBluetooth(resultLauncher: ActivityResultLauncher<Intent>){
        if (bluetoothAdapter?.isEnabled == false) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(intent)
        }
    }

    fun queryDevices():ArrayList<BluetoothDevice>{
        val listItems = ArrayList<BluetoothDevice>()
//        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
//        pairedDevices?.forEach { device ->
//            if (device.type != BluetoothDevice.DEVICE_TYPE_LE)
//                listItems.add(device)
//        }
        Collections.sort(listItems, ::compareTo)
        return listItems
    }


    fun startServerThread(mHandler: Handler) {
        bluetoothAdapter?.let {
            bluetoothService = BluetoothService(it)
            bluetoothService?.setHandler(mHandler)
            serverThread = bluetoothService?.ServerClass()
            serverThread?.start()
        }
    }

    fun startConnection(bluetoothDevice: BluetoothDevice) {
        bluetoothService?.ClientClass(bluetoothDevice)?.start()
    }

    fun sendData(message : String){
        bluetoothService?.write(message)
    }

    fun changeHandler(handler: Handler){
        bluetoothService?.setHandler(handler)
    }

    fun disconnect(){
        serverThread?.closeServerSocket()
        bluetoothService?.disconnect()

    }

}