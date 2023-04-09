package com.hbeonlabs.driversalerts.utils

import android.hardware.SensorManager
import android.os.Looper
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

class DriverLocationProvider (val fragment : Fragment, onLocationChange:(locationData:LocationAndSpeed)->Unit ) {
    private lateinit var sensorManager: SensorManager
    private lateinit var lastLocation: LocationResult
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var curSpeed = 0f
    private var lastSpeed = 0f
    private var acceleration = 0f

    private val _speedEvent= MutableSharedFlow<Float>()
    val speedEvent: SharedFlow<Float> = _speedEvent

    private val _speedEvent2= MutableSharedFlow<Float>()
    val speedEvent2: SharedFlow<Float> = _speedEvent2

    private val _accelerationEvent= MutableSharedFlow<Float>()
    val accelerationEvent: SharedFlow<Float> = _accelerationEvent

    private var locationRequest= LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //TODO send this location in socket
            fragment.lifecycleScope.launchWhenStarted {
                onLocationChange(LocationAndSpeed(
                    locationLatitude = locationResult.lastLocation?.latitude.toString(),
                    locationLongitude = locationResult.lastLocation?.longitude.toString(),
                    speed = locationResult.lastLocation?.speed.toString(),
                    timeInMills = android.icu.util.Calendar.getInstance().timeInMillis.toString()
                ))
            }
            locationResult.let {
                lastLocation = it

            }
        }
    }

    init {
        startLocationUpdate()
       // getAccelerometerData()
    }


    /**
     * Request location update
     */
    private fun startLocationUpdate() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(fragment.requireActivity())
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            // lacking permission to access location
        }
    }


    /**
     * Stop location updates
     */
    private fun stopLocationsUpdate() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun onDestroy() {
        stopLocationsUpdate()
    }


    fun calculateAccelerationWithinThreshold()
    {
        fragment.lifecycleScope.launchWhenStarted {
            while (true)
            {
                delay(2000)
                speedEvent2.collectLatest {
                    acceleration = it - lastSpeed
                    lastSpeed  = it
                    _accelerationEvent.emit(acceleration)
                }
            }
        }
    }

}