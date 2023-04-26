package com.hbeonlabs.driversalerts.data.repository

import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.LocationAndSpeedDao
import com.hbeonlabs.driversalerts.data.local.db.NotificationDao
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.local.persistance.PrefManager
import com.hbeonlabs.driversalerts.data.remote.api.AppApis
import com.hbeonlabs.driversalerts.data.remote.request.CreateNotificationDTO
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import com.hbeonlabs.driversalerts.utils.network.onError
import com.hbeonlabs.driversalerts.utils.network.onException
import com.hbeonlabs.driversalerts.utils.network.onSuccess
import com.hbeonlabs.driversalerts.utils.timeInMillsToDate
import com.hbeonlabs.driversalerts.utils.timeInMillsToTime
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val locationDao : LocationAndSpeedDao,
    private val notificationDao: NotificationDao,
    private val appApis: AppApis,
    private val prefManager: PrefManager

) {

    suspend fun addLocationData(locationAndSpeed: LocationAndSpeed)
    {
        locationDao.addData(locationAndSpeed)

    }


    suspend fun addNotification(notification: Warning)
    {
        val driverData = prefManager.getDeviceConfigurationDetails()

        val notificationRequest = CreateNotificationDTO(
            date = notification.timeInMills.toLong().timeInMillsToDate(),
            routeId = 12,
            schoolId = driverData.schoolId,
            latitude = notification.locationLatitude,
            description = notification.message,
            vehicleId = driverData.vehicleId,
            time =  notification.timeInMills.toLong().timeInMillsToTime(),
            title = notification.notificationTitle,
            type = 1,
            longitude = notification.locationLongitude


        )
        appApis.sendNotificationData(notificationRequest).onSuccess {
            notification.isSynced = true
        }.onError { code, message ->
            notification.isSynced = false
        }.onException {
            notification.isSynced = false
        }
        notificationDao.addNotification(notification)
    }

    fun getNotificationList() = notificationDao.getAllNotifications()

    suspend fun getAllNotificationsFromApi() = appApis.getAllNotifications()

    suspend fun fetchDeviceConfigurationFromServer(){
        appApis.getDeviceConfigurationDetails(prefManager.getDeviceConfigurationDetails().deviceId?:"").onSuccess {
            prefManager.saveDeviceConfigurationDetails(it)
        }
    }

    fun fetchDeviceConfiguration():DeviceConfigurationResponse
    {
        return prefManager.getDeviceConfigurationDetails()
    }


    suspend fun createAttendance(attendance: AttendanceModel) = appApis.addAttendance(attendance)

    fun getLast5LocationListFromDB() = locationDao.getLast5Items()


}