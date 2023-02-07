package com.hbeonlabs.driversalerts.ui.fragment.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(

) : ViewModel() {

    private val _cameraEvents = MutableSharedFlow<CameraStates>()
    val cameraEvents: SharedFlow<CameraStates> = _cameraEvents


    fun drowsinessDetected() {
        viewModelScope.launch {
            _cameraEvents.emit(CameraStates.DrowsinessDetectedState)
        }
    }

    fun drowsinessGone() {
        viewModelScope.launch {
            _cameraEvents.emit(CameraStates.DrowsinessGoneState)
        }
    }


}

sealed class CameraStates() {
    object DrowsinessDetectedState : CameraStates()
    object DrowsinessGoneState : CameraStates()
}