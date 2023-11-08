package com.hbeonlabs.driversalerts.utils

import android.os.Environment
import android.util.Log
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    val dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
    val formatWithDate = SimpleDateFormat("dd-MM-yyyy_hh:mm:ss_a", Locale.getDefault())

    fun makeOutputMediaFile(): String? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES
            ), "MyCameraApp"
        )
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory")
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = mediaStorageDir.path + File.separator + "Recording_" + timeStamp + ".mp4"
        val mediaFile = File(fileName)
        mediaFile.createNewFile()
        if(mediaFile.isFile && mediaFile.exists())
            return fileName
        else
            return null
    }

    fun getCurrentTimeString() = dateFormat.format(System.currentTimeMillis())
    fun getCurrentDateTimeString() = formatWithDate.format(System.currentTimeMillis())

    fun getRoomName(deviceConfigs: DeviceConfigurationResponse?, isFront: Boolean): String {
        val deviceName = if (isFront) "FRONT" else "BACK"
        return "${deviceConfigs?.schoolId}_${deviceConfigs?.vehicleId}_${deviceName}"
    }

}