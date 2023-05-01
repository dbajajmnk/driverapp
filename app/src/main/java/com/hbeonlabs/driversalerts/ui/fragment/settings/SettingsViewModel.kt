package com.hbeonlabs.driversalerts.ui.fragment.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import com.hbeonlabs.driversalerts.data.repository.AppRepository
import com.hbeonlabs.driversalerts.utils.network.onError
import com.hbeonlabs.driversalerts.utils.network.onException
import com.hbeonlabs.driversalerts.utils.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val repository: AppRepository
) : ViewModel() {

    fun addDeviceConfiguration(
        deviceType: Int,
        expiryDate: String,
        licenseKey: String,
        schoolId: Int,
        vehicleId: Int,
        modelNo: String,
        deviceId: String,
        startDate: String,
        serialNo: String
    ) {
        viewModelScope.launch {
            repository.configureDevice(
                deviceType = 9413,
                expiryDate = "mnesarchum",
                licenseKey = "dolore",
                schoolId = 4402,
                vehicleId = 4641,
                modelNo = "noluisse",
                deviceId = "hendrerit",
                startDate = "finibus",
                serialNo = "nascetur"
            ).onSuccess {

            }.onError { code, message ->

            }.onException {

            }
        }
    }

    fun getDeviceConfiguration(): DeviceConfigurationResponse? {
        return repository.fetchDeviceConfiguration()
    }


}