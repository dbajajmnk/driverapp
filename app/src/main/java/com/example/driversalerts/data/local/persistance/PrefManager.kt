package com.example.driversalerts.data.local.persistance

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

class PrefManager(context: Context) {
    private val sharedPreferences : SharedPreferences = context.getSharedPreferences("DriverAlert", Context.MODE_PRIVATE)
    private val audioUriKey = "audioUriKey"
    private val durationKey = "durationKey"
    private val autoRebootEnabledKey = "autoRebootEnabledKey"
    private val cameraKey = "cameraKey"
    private val licenseActivatedKey = "licenseActivatedKey"

    fun saveAudioUri(uri : Uri?){
        val value = uri?.toString() ?: ""
        sharedPreferences.edit().
        putString(audioUriKey,value).
        commit()
    }

    fun getAudioUri():Uri?{
        val uriString = sharedPreferences.getString(audioUriKey, "")
        return if(uriString.isNullOrEmpty())
            null
        else
            Uri.parse(uriString)
    }

    fun saveDuration(duration : Int){
        sharedPreferences.edit().
        putInt(durationKey,duration).
        commit()
    }

    fun getDuration():Int{
        return sharedPreferences.getInt(durationKey, 500)
    }

    fun saveCameraSelected(cameraSelected : Int){
        sharedPreferences.edit().
        putInt(cameraKey,cameraSelected).
        commit()
    }

    fun getCameraSelected():Int{
        return sharedPreferences.getInt(cameraKey, 0)
    }

    fun saveAutoStartEnabled(autoStart : Boolean){
        sharedPreferences.edit().
        putBoolean(autoRebootEnabledKey,autoStart).
        commit()
    }

    fun getAutoStartEnabled():Boolean{
        return sharedPreferences.getBoolean(autoRebootEnabledKey, true)
    }

    fun saveLicenseActivated(licenseActivated : Boolean){
        sharedPreferences.edit().
        putBoolean(licenseActivatedKey,licenseActivated).
        commit()
    }

    fun getLicenseActivated():Boolean{
        return sharedPreferences.getBoolean(licenseActivatedKey, false)
    }
}