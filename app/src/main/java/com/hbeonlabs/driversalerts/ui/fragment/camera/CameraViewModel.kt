package com.hbeonlabs.driversalerts.ui.fragment.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    val repository: AppRepository

) : ViewModel() {



    fun addLocationData(locationAndSpeed: LocationAndSpeed)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addLocationData(locationAndSpeed)
        }
    }
    fun addWarningsData(notification: Warning)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNotification(notification)
        }
    }

    fun addAttendance(attendance: AttendanceModel)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createAttendance(attendance)
        }
    }



}