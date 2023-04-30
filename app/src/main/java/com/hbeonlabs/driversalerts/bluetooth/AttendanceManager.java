package com.hbeonlabs.driversalerts.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayDeque;

public class AttendanceManager implements ServiceConnection, SerialListener, DefaultLifecycleObserver {

    private enum Connected { False, Pending, True }

    private String deviceAddress;
    private SerialService service;

    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private String newline = TextUtil.newline_crlf;
    private FragmentActivity activity;
    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private AttendanceModel attendanceModel;
    private AttendanceCallback attendanceCallback;
    public AttendanceManager(FragmentActivity activity, AttendanceCallback attendanceCallback){
        this.activity = activity;
        this.attendanceCallback = attendanceCallback;
        getDeviceAddress(activity);
        onAttach(activity);
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        onDetach();
        if (connected != Connected.False)
            disconnect();
        activity.stopService(new Intent(activity, SerialService.class));
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        if(service != null)
            service.attach(this);
        else
            activity.startService(new Intent(activity, SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStop(owner);
        if(service != null && !activity.isChangingConfigurations())
            service.detach();
    }

    public void onAttach(@NonNull Activity activity) {
        activity.bindService(new Intent(activity, SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    public void onDetach() {
        try { activity.unbindService(this); } catch(Exception ignored) {}
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        if(initialStart && service != null && deviceAddress != null) {
            initialStart = false;
            activity.runOnUiThread(this::connect);
        }
    }

    private void getDeviceAddress(LifecycleOwner owner){
        if(deviceAddress == null) {
            if(deviceAddress == null) {
                initLaunchers(owner, activity.getActivityResultRegistry());
            }
            if (BluetoothUtil.hasPermissions(activity, permissionLauncher)) {
                this.deviceAddress = BluetoothUtil.getAttendanceDeviceAddress(activity, enableBluetoothLauncher);
            }
        }
    }
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart) {  //TODO Add check of is resumed or not
            initialStart = false;
            activity.runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            Toast.makeText(activity,"connecting...",Toast.LENGTH_SHORT).show();
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(activity.getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    private void send(String str) {
        if(connected != Connected.True) {
            Toast.makeText(activity, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;
            if(hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();
            }
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(ArrayDeque<byte[]> datas) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        for (byte[] data : datas) {
            if (hexEnabled) {
                spn.append(TextUtil.toHexString(data)).append('\n');
            } else {
                send("Y");
                String msg = new String(data);
                sendCallback(msg);
            }
        }
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        connected = Connected.True;
        Toast.makeText(activity,"onSerialConnect",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        disconnect();
        Toast.makeText(activity,"onSerialConnectError",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSerialRead(byte[] data) {
        ArrayDeque<byte[]> datas = new ArrayDeque<>();
        datas.add(data);
        receive(datas);
    }

    public void onSerialRead(ArrayDeque<byte[]> datas) {
        receive(datas);
    }

    @Override
    public void onSerialIoError(Exception e) {
        disconnect();
        Toast.makeText(activity,"onSerialIoError",Toast.LENGTH_SHORT).show();
    }

    private void initLaunchers(LifecycleOwner owner, ActivityResultRegistry registry) {
        try {
            permissionLauncher = registry.register("1", owner, new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    deviceAddress = BluetoothUtil.getAttendanceDeviceAddress(activity, enableBluetoothLauncher);
                }
            });

            enableBluetoothLauncher = registry.register("2", owner, new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    deviceAddress = BluetoothUtil.getAttendanceDeviceAddress(activity, enableBluetoothLauncher);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendCallback(String msg){
        Log.v("AttendanceManager", "Received msg : "+msg);
        if(msg != null && msg.contains(",")){
            msg = msg.trim();
            String[] items = msg.split(",");
            String deviceId = "", tagId = "", date = "", time = "";
            for(String item : items) {
                if(item != null && item.contains("=")){
                    String[] itemValue = item.split("=");
                    switch (itemValue[0]){
                        case (Constants.DEVICE_ID):{
                            deviceId = itemValue[1];
                            break;
                        }
                        case (Constants.TAG_ID):{
                            tagId = itemValue[1];
                            break;
                        }
                        case (Constants.DATE):{
                            date = itemValue[1];
                            break;
                        }
                        case (Constants.TIME):{
                            time = itemValue[1];
                            break;
                        }
                    }
                }
            }
            AttendanceModel newAttendanceModel = new AttendanceModel(deviceId,tagId,date,time);
            if(!newAttendanceModel.equals(attendanceModel)){
                attendanceModel = newAttendanceModel;
                attendanceCallback.onAttendance(attendanceModel);
            }
        }
    }
}

