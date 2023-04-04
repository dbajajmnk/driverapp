package com.hbeonlabs.driversalerts.utils

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class DriverLocationProvider (val activity : AppCompatActivity, onLocationChange:(locationData:LocationAndSpeed)->Unit ) {
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
            activity.lifecycleScope.launchWhenStarted {
                val speed = locationResult.lastLocation?.speed ?:0f
                _speedEvent.emit(speed)
                _speedEvent2.emit(speed)
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

/*    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                activity.lifecycleScope.launchWhenStarted {
                    _accelerationEvent.emit( event.values[0])
                }
              //  val currentAccel = event.values[0] // Assuming X-axis accelerometer data
             *//*   val deltaAccel = currentAccel - lastAccel
                if (deltaAccel > 5f) { // Threshold for harsh acceleration
                    harshDrivingCount++
                }
                lastAccel = currentAccel*//*
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

    }*/


    init {
        startLocationUpdate()
       // getAccelerometerData()
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

/*    private fun getAccelerometerData(){
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
        }
    }*/


    fun calculateAccelerationWithinThreshold()
    {
        activity.lifecycleScope.launchWhenStarted {
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