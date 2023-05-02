package com.hbeonlabs.driversalerts.ui.fragment.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import com.hbeonlabs.driversalerts.data.repository.AppRepository
import com.hbeonlabs.driversalerts.utils.network.onError
import com.hbeonlabs.driversalerts.utils.network.onException
import com.hbeonlabs.driversalerts.utils.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

     val showProgressBarLiveData = MutableLiveData(false)

    fun addDeviceConfiguration(
        licenseKey: String,
        deviceId: String,
        vehicleId: String,
        bluetoothId: String
    ) {
        showProgressBarLiveData.value = true
        viewModelScope.launch {
            repository.configureDevice(
                deviceType = 9413,
                expiryDate = "04-12-2025",
                licenseKey = licenseKey,
                schoolId = 4402,
                vehicleId = vehicleId,
                modelNo = "noluisse",
                deviceId = deviceId,
                startDate = "finibus",
                serialNo = "nascetur",
                bluetoothId = bluetoothId
            ).onSuccess {
                showProgressBarLiveData.value = false
                repository.saveDeviceConfigId(deviceId)
            }.onError { code, message ->
                showProgressBarLiveData.value = false
            }.onException {
                showProgressBarLiveData.value = false
            }
        }
    }

    fun getDeviceConfiguration(): DeviceConfigurationResponse? {
        return repository.fetchDeviceConfiguration()
    }
}