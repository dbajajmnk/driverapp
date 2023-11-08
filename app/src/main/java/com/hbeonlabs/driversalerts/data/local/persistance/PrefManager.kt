package com.hbeonlabs.driversalerts.data.local.persistance

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import com.google.gson.Gson
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import javax.inject.Inject

class PrefManager @Inject constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("DriverAlert", Context.MODE_PRIVATE)
    private val audioUriKey = "audioUriKey"
    private val durationKey = "durationKey"
    private val autoRebootEnabledKey = "autoRebootEnabledKey"
    private val cameraKey = "cameraKey"
    private val licenseActivatedKey = "licenseActivatedKey"
    private val locationFequencyKey = "locationFequencyKey"
    private val deviceConfigurationKey = "deviceConfigurationKey"
    private val deviceConfigIdKey = "deviceConfigIdKey"
    private val FRONT_ROOM = "FrontRoom"
    private val BACK_ROOM = "BackRoom"
    fun saveAudioUri(uri: Uri?) {
        val value = uri?.toString() ?: ""
        sharedPreferences.edit().putString(audioUriKey, value).apply()
    }

    fun saveDeviceConfigurationDetails(deviceConfigurationResponse: DeviceConfigurationResponse){
        val deviceConfigurationResponseInString = Gson().toJson(deviceConfigurationResponse)
        sharedPreferences.edit().putString(deviceConfigurationKey, deviceConfigurationResponseInString).apply()
    }

    fun getDeviceConfigurationDetails():DeviceConfigurationResponse?{
        val deviceConfigurationResponseInString = sharedPreferences.getString(deviceConfigurationKey,"")
        if (deviceConfigurationResponseInString.isNullOrEmpty())
        {
            return null
        }
        return  Gson().fromJson(deviceConfigurationResponseInString,DeviceConfigurationResponse::class.java)
    }

    fun getAudioUri(): Uri? {
        val uriString = sharedPreferences.getString(audioUriKey, "")
        return if (uriString.isNullOrEmpty())
            null
        else
            Uri.parse(uriString)
    }

    fun saveDuration(duration: Int) {
        sharedPreferences.edit().putInt(durationKey, duration).apply()
    }

    fun getDuration(): Int {
        return sharedPreferences.getInt(durationKey, 500)
    }

    fun saveCameraSelected(cameraSelected: Int) {
        sharedPreferences.edit().putInt(cameraKey, cameraSelected).apply()
    }

    fun getCameraSelected(): Int {
        return sharedPreferences.getInt(cameraKey, 0)
    }

    fun saveAutoStartEnabled(autoStart: Boolean) {
        sharedPreferences.edit().putBoolean(autoRebootEnabledKey, autoStart).apply()
    }

    fun getAutoStartEnabled(): Boolean {
        return sharedPreferences.getBoolean(autoRebootEnabledKey, true)
    }

    fun saveLicenseActivated(licenseActivated: Boolean) {
        sharedPreferences.edit().putBoolean(licenseActivatedKey, licenseActivated).apply()
    }

    fun getLicenseActivated(): Boolean {
        return sharedPreferences.getBoolean(licenseActivatedKey, false)
    }

    fun saveLocationFrequency(duration: Int) {
        sharedPreferences.edit().putInt(locationFequencyKey, duration). apply()
    }

    fun getLocationFrequency(): Int {
        return sharedPreferences.getInt(locationFequencyKey, 5000)
    }

    fun saveDeviceConfigId(deviceConfigId: String) {
        sharedPreferences.edit().putString(deviceConfigIdKey, deviceConfigId). apply()
    }

    fun getDeviceConfigId(): String? {
        return sharedPreferences.getString(deviceConfigIdKey, "")
    }


    fun getLiveKitFrontRoom(): String = sharedPreferences.getString(FRONT_ROOM, "FrontRoom2").toString()

    fun saveLiveKitFrontRoom(roomName: String) {
        sharedPreferences.edit {
            putString(FRONT_ROOM, roomName)
        }
    }

    fun getLiveKitBackRoom(): String = sharedPreferences.getString(BACK_ROOM, "BackRoom2").toString()

    fun saveLiveKitBackRoom(roomName: String) {
        sharedPreferences.edit {
            putString(BACK_ROOM,roomName)
        }
    }

}