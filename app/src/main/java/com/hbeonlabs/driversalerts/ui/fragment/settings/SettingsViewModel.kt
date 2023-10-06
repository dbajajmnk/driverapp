package com.hbeonlabs.driversalerts.ui.fragment.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbeonlabs.driversalerts.data.local.persistance.PrefManager
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
    private val repository: AppRepository,
    private val pref: PrefManager,
) : ViewModel() {

     val showProgressBarLiveData = MutableLiveData(false)

    fun addDeviceConfiguration(
        licenseKey: String,
        deviceId: String,
        bluetoothId: String,
    ) {
      val configDetails = pref.getDeviceConfigurationDetails()
        showProgressBarLiveData.value = true
        viewModelScope.launch {
            repository.configureDevice(
                deviceType = configDetails?.deviceType ?: 1,
                licenseKey = licenseKey,
                modelNo = configDetails?.modelNo ?: "",
                deviceId = deviceId,
                serialNo = configDetails?.serialNo ?: "",
                bluetoothId = bluetoothId,
            ).onSuccess {
                showProgressBarLiveData.value = false
                repository.saveDeviceConfigId(deviceId)
            }.onError { _, message ->
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
