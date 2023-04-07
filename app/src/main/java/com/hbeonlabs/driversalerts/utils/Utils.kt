package com.hbeonlabs.driversalerts.utils

import android.os.Environment
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Utils {

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
}