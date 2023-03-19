package com.hbeonlabs.driversalerts.utils

import android.app.Activity
import android.os.Looper
import com.google.android.gms.location.*
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper

class DriverLocationProvider(val activity: Activity, val webRtcHelper : WebRtcHelper) {
    private lateinit var lastLocation: LocationResult
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest= LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //TODO send this location in socket
            locationResult?.let {
                lastLocation = it
                monitorSpeed()
            }
        }
    }

    init {
        startLocationUpdate()
    }
    /**
     * Request location update
     */
    private fun startLocationUpdate() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
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

    fun monitorSpeed(){

    }

}