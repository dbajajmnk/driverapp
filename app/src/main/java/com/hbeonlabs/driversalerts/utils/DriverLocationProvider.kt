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
import com.hbeonlabs.driversalerts.webrtc.WebRtcHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class DriverLocationProvider (val activity : AppCompatActivity) {
    private lateinit var sensorManager: SensorManager
    private lateinit var lastLocation: LocationResult
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _speedEvent= MutableStateFlow(0f)
    val speedEvent: SharedFlow<Float> = _speedEvent

    private val _accelerationEvent= MutableStateFlow(0f)
    val accelerationEvent: SharedFlow<Float> = _accelerationEvent

    private var locationRequest= LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //TODO send this location in socket
            activity.lifecycleScope.launchWhenStarted {
                _speedEvent.emit(locationResult.lastLocation?.speed ?:0f)
            }
            locationResult.let {
                lastLocation = it
                monitorSpeed()
            }
        }
    }

    private val accelerometerListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                activity.lifecycleScope.launchWhenStarted {
                    _accelerationEvent.emit( event.values[0])
                }
              //  val currentAccel = event.values[0] // Assuming X-axis accelerometer data
             /*   val deltaAccel = currentAccel - lastAccel
                if (deltaAccel > 5f) { // Threshold for harsh acceleration
                    harshDrivingCount++
                }
                lastAccel = currentAccel*/
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
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

    fun getAccelerometerData(){
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI)
        }
    }

}