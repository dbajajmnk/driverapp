package com.hbeonlabs.driversalerts.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.hbeonlabs.driversalerts.R
import com.hbeonlabs.driversalerts.databinding.FragmentWatcherBinding
import com.hbeonlabs.driversalerts.utils.DriverLocationProvider
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper
import kotlinx.coroutines.flow.collectLatest

class AdminActivity : AppCompatActivity(){
    lateinit var locationProvider : DriverLocationProvider

    private var locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<FragmentWatcherBinding>(this, R.layout.fragment_watcher)
        WebRtcHelper.getInstance().init(this)
        WebRtcHelper.getInstance().startReceiverStreaming(binding.surfaceview)
        checkPermissions()
        //WebRtcHelper.getInstance().start(this, null,binding.surfaceview)
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // request for permissions
    private fun requestPermissions() {
        permissionRequest.launch(locationPermissions)
    }

    // Permission result
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val granted = permissions.entries.all {
            it.value
        }
        permissions.entries.forEach {
            Log.e("LOG_TAG", "${it.key} = ${it.value}")
        }
        if (granted) {
            // your code if permission granted
            doOnLocationPermissionAvailable()
        } else {
            // your code if permission denied
            requestPermissions()
        }
    }

    private fun doOnLocationPermissionAvailable() {
        locationProvider = DriverLocationProvider(this)

        lifecycleScope.launchWhenStarted {
            locationProvider.speedEvent.collectLatest {
                Log.d("TAG", "doOnLocationPermissionAvailable: $it")
            }

        }
    }

}