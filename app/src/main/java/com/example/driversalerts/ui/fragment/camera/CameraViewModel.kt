package com.example.driversalerts.ui.fragment.camera

import android.app.Application
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutionException

@HiltViewModel
class CameraViewModel constructor(

) : ViewModel(){

    private val TAG = "CameraXViewModel"
    private var cameraProviderLiveData: MutableLiveData<ProcessCameraProvider>? = null

    fun getProcessCameraProvider(): LiveData<ProcessCameraProvider>? {
        if (cameraProviderLiveData == null) {
            cameraProviderLiveData = MutableLiveData()
            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(getApplication())
            cameraProviderFuture.addListener(
                {
                    try {
                        cameraProviderLiveData!!.setValue(cameraProviderFuture.get())
                    } catch (e: ExecutionException) {
                        // Handle any errors (including cancellation) here.
                        Log.e(TAG, "Unhandled exception", e)
                    } catch (e: InterruptedException) {
                        Log.e(TAG, "Unhandled exception", e)
                    }
                },
                ContextCompat.getMainExecutor(getApplication())
            )
        }
        return cameraProviderLiveData
    }

}