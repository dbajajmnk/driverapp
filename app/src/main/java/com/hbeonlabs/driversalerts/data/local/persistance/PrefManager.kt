package com.hbeonlabs.driversalerts.data.local.persistance

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.google.gson.Gson
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse

class PrefManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("DriverAlert", Context.MODE_PRIVATE)
    private val audioUriKey = "audioUriKey"
    private val durationKey = "durationKey"
    private val autoRebootEnabledKey = "autoRebootEnabledKey"
    private val cameraKey = "cameraKey"
    private val licenseActivatedKey = "licenseActivatedKey"
    private val locationFequencyKey = "locationFequencyKey"
    private val deviceConfigurationKey = "deviceConfigurationKey"

    fun saveAudioUri(uri: Uri?) {
        val value = uri?.toString() ?: ""
        sharedPreferences.edit().putString(audioUriKey, value).apply()
    }

    fun saveDeviceConfigurationDetails(deviceConfigurationResponse: DeviceConfigurationResponse){
        val deviceConfigurationResponseInString = Gson().toJson(deviceConfigurationResponse)
        sharedPreferences.edit().putString(deviceConfigurationKey, deviceConfigurationResponseInString).apply()
    }

    fun getDeviceConfigurationDetails():DeviceConfigurationResponse{
        val deviceConfigurationResponseInString = sharedPreferences.getString(deviceConfigurationKey,"")
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

}