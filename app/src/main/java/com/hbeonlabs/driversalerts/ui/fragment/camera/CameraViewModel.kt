package com.hbeonlabs.driversalerts.ui.fragment.camera

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    val repository: AppRepository

) : ViewModel() {

    init {
        calculateAcceleration()
    }

    private val speedFlow = MutableSharedFlow<LocationAndSpeed>()

    @OptIn(FlowPreview::class)
    fun calculateAcceleration(){
        var oldSpeed = 0f
        var acceleration: Float
        viewModelScope.launch (Dispatchers.IO){
            speedFlow.debounce(2000).collect {
                acceleration = (it.speed.toFloat() - oldSpeed)/1
                oldSpeed = it.speed.toFloat()
                Log.d("TAG", "calculateAcceleration: "+acceleration)
            }
        }

    }


    fun addLocationData(locationAndSpeed: LocationAndSpeed)
    {
        viewModelScope.launch(Dispatchers.IO) {
            speedFlow.emit(locationAndSpeed)
            repository.addLocationData(locationAndSpeed)
        }
    }
    fun createNotification(warning: Warning)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNotification(warning)
        }
    }

    fun addAttendance(attendance: AttendanceModel)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createAttendance(attendance)
        }
    }

    fun getLast5LocationData() = repository.getLast5LocationListFromDB()

    fun getDeviceConfiguration(){
        viewModelScope.launch {
            repository.fetchDeviceConfigurationFromServer()
        }
    }



}