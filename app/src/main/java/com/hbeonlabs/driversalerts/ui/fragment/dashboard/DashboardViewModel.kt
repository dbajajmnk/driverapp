package com.hbeonlabs.driversalerts.ui.fragment.dashboard

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.remote.request.CreateTokenRequestModel
import com.hbeonlabs.driversalerts.data.remote.response.CreateTokenResponseModel
import com.hbeonlabs.driversalerts.data.remote.response.RoomCreationResponseModel
import com.hbeonlabs.driversalerts.data.repository.AppRepository
import com.hbeonlabs.driversalerts.utils.network.onError
import com.hbeonlabs.driversalerts.utils.network.onException
import com.hbeonlabs.driversalerts.utils.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {
    val createRoomLiveData = MutableLiveData<RoomCreationResponseModel?>(null)
    val createTokenLiveData = MutableLiveData<CreateTokenResponseModel?>(null)
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

        }
    }

    fun getLast5LocationData() = repository.getLast5LocationListFromDB()

    fun getDeviceConfiguration(){
        viewModelScope.launch {
            repository.fetchDeviceConfigurationFromServer()
        }
    }


    fun createRoom(roomName : String){
        viewModelScope.launch {
            repository.createRoom().onSuccess {
                createRoomLiveData.postValue(it)
            }.onError { code, message ->
                createRoomLiveData.postValue(RoomCreationResponseModel(null, "Error:$code $message"))
            }.onException {
                createRoomLiveData.postValue(RoomCreationResponseModel(null, "Error:${it.message}"))
            }
        }
    }

    fun createToken(roomName : String, participantName : String){
        viewModelScope.launch {
            val createTokenRequestModel = CreateTokenRequestModel(participantName, roomName, "Driver")
            repository.createToken(createTokenRequestModel).onSuccess {
                createTokenLiveData.postValue(it.apply { it.roomName = roomName })
            }.onError { code, message ->
                createTokenLiveData.postValue(CreateTokenResponseModel("Error:$code $message",null, roomName))
            }.onException {
                createTokenLiveData.postValue(CreateTokenResponseModel("Error:${it.message}",null,  roomName))
            }
        }
    }
}