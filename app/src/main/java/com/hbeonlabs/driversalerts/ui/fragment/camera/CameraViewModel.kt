package com.hbeonlabs.driversalerts.ui.fragment.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Notification
import com.hbeonlabs.driversalerts.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    fun addWarningsData(notification: Notification)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNotification(notification)
        }
    }

}