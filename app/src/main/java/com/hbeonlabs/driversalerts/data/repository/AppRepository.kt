package com.hbeonlabs.driversalerts.data.repository

import com.hbeonlabs.driversalerts.data.local.db.LocationAndSpeedDao
import com.hbeonlabs.driversalerts.data.local.db.WarningsDao
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Notification
import com.hbeonlabs.driversalerts.data.local.persistance.PrefManager
import com.hbeonlabs.driversalerts.data.remote.api.AppApis
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import com.hbeonlabs.driversalerts.utils.network.onSuccess
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val locationDao : LocationAndSpeedDao,
    private val warningsDao: WarningsDao,
    private val appApis: AppApis,
    private val prefManager: PrefManager

) {

    suspend fun addLocationData(locationAndSpeed: LocationAndSpeed)
    {
        locationDao.addData(locationAndSpeed)

    }

    suspend fun addWarnings(warning: Notification)
    {
        warningsDao.addNotification(warning)
    }

    fun getWarningsList() = warningsDao.getAllNotifications()

    suspend fun getAllNotificationsFromApi() = appApis.getAllNotifications()

    suspend fun fetchDeviceConfigurationFromServer(){
        appApis.getDeviceConfigurationDetails("20").onSuccess {
            prefManager.saveDeviceConfigurationDetails(it)
        }
    }

    fun fetchDeviceConfiguration():DeviceConfigurationResponse
    {
        return prefManager.getDeviceConfigurationDetails()
    }


}