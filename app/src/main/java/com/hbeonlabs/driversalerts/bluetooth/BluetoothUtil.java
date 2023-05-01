package com.hbeonlabs.driversalerts.bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.hbeonlabs.driversalerts.BuildConfig;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothUtil {

    interface PermissionGrantedCallback {
        void call();
    }

    /**
     * sort by name, then address. sort named devices first
     */
    @SuppressLint("MissingPermission")
    static int compareTo(BluetoothDevice a, BluetoothDevice b) {
        boolean aValid = a.getName() != null && !a.getName().isEmpty();
        boolean bValid = b.getName() != null && !b.getName().isEmpty();
        if (aValid && bValid) {
            int ret = a.getName().compareTo(b.getName());
            if (ret != 0) return ret;
            return a.getAddress().compareTo(b.getAddress());
        }
        if (aValid) return -1;
        if (bValid) return +1;
        return a.getAddress().compareTo(b.getAddress());
    }

    /**
     * Android 12 permission handling
     */
    private static void showRationaleDialog(Activity activity, DialogInterface.OnClickListener listener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Give Permission");
        builder.setMessage("Please grant permission for attendance");
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Continue", listener);
        builder.show();
    }

    private static void showSettingsDialog(Activity activity) {
        String s = activity.getResources().getString(activity.getResources().getIdentifier("@android:string/permgrouplab_nearby_devices", null, null));
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Give Permission");
        builder.setMessage(String.format("Bluetooth permission was permanently denied. You have to enable permission \"%s\" in App settings.", s));
        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Settings", (dialog, which) ->
                activity.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID))));
        builder.show();
    }

    public static boolean checkPermissions(Activity activity){
        return activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }
    public static boolean hasPermissions(Activity activity, ActivityResultLauncher<String> requestPermissionLauncher) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            return true;
        boolean hasPermissions = checkPermissions(activity);
        boolean showRationale = activity.shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT);

        if (!hasPermissions) {
            if (showRationale) {
                showRationaleDialog(activity, (dialog, which) ->
                        requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT));
            } else {
                requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
            }
            return false;
        } else {
            return true;
        }
    }

    public static void onPermissionsResult(Activity activity, boolean granted, PermissionGrantedCallback cb) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S)
            return;
        boolean showRationale = activity.shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT);
        if (granted) {
            cb.call();
        } else if (showRationale) {
            showRationaleDialog(activity, (dialog, which) -> cb.call());
        } else {
            showSettingsDialog(activity);
        }
    }


    private static BluetoothAdapter initBluetoothAdapter(Activity activity, ActivityResultLauncher<Intent> resultLauncher) {
        BluetoothManager bluetoothManager = activity.getSystemService(BluetoothManager.class);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(activity, "Bluetooth not supported in this device.", Toast.LENGTH_SHORT).show();
            // Device doesn't support Bluetooth
        }
        return bluetoothAdapter;
    }

    public static String getAttendanceDeviceAddress(Activity activity, ActivityResultLauncher<Intent> resultLauncher) {
        BluetoothAdapter bluetoothAdapter = initBluetoothAdapter(activity, resultLauncher);
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            resultLauncher.launch(intent);
        }
        String deviceAddress = null;
        if (bluetoothAdapter != null) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Please provide bluetooth permissions for attendance.", Toast.LENGTH_SHORT).show();
                return null;
            }
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("BLE_RFID_DEVICE")) {
                    deviceAddress = device.getAddress();
                }
            }
        }
        return deviceAddress;
    }

}
