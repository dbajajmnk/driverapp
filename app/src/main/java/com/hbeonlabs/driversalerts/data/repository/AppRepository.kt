package com.hbeonlabs.driversalerts.data.repository

import android.util.Log
import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.AttendanceDao
import com.hbeonlabs.driversalerts.data.local.db.LocationAndSpeedDao
import com.hbeonlabs.driversalerts.data.local.db.NotificationDao
import com.hbeonlabs.driversalerts.data.local.db.models.Attendance
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import com.hbeonlabs.driversalerts.data.local.persistance.PrefManager
import com.hbeonlabs.driversalerts.data.mappers.toCreateAttendanceRequest
import com.hbeonlabs.driversalerts.data.remote.api.AppApis
import com.hbeonlabs.driversalerts.data.remote.request.ConfigureDeviceRequest
import com.hbeonlabs.driversalerts.data.remote.request.CreateAttendanceRequest
import com.hbeonlabs.driversalerts.data.remote.request.CreateNotificationDTO
import com.hbeonlabs.driversalerts.data.remote.request.CreateRoomRequestModel
import com.hbeonlabs.driversalerts.data.remote.request.CreateTokenRequestModel
import com.hbeonlabs.driversalerts.data.remote.response.AttendanceListResponseItem
import com.hbeonlabs.driversalerts.data.remote.response.BasicMessageResponse
import com.hbeonlabs.driversalerts.data.remote.response.CreateTokenResponseModel
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import com.hbeonlabs.driversalerts.data.remote.response.RoomCreationResponseModel
import com.hbeonlabs.driversalerts.utils.network.NetworkResult
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
    private val prefManager: PrefManager,
    private val attendanceDao: AttendanceDao
) {

    suspend fun addLocationData(locationAndSpeed: LocationAndSpeed)
    {
        locationDao.addData(locationAndSpeed)
    }

    suspend fun addNotification(warning: Warning)
    {
        val driverData = fetchDeviceConfiguration()
        Log.d("TAG", "addNotification: "+driverData)
        if (driverData!=null)
        {
            val notificationRequest = CreateNotificationDTO(
                date = warning.timeInMills.toLong().timeInMillsToDate(),
                routeId = 12,
                schoolId = driverData.schoolId,
                latitude = warning.locationLatitude,
                description = warning.message,
                vehicleId = driverData.vehicleId,
                time =  warning.timeInMills.toLong().timeInMillsToTime(),
                title = warning.notificationTitle,
                type = warning.notificationType,
                longitude = warning.locationLongitude
            )
            appApis.sendNotificationData(notificationRequest).onSuccess {
                warning.isSynced = true
            }.onError { code, message ->
                warning.isSynced = false
            }.onException {
                warning.isSynced = false
            }
            notificationDao.addNotification(warning)
        }
    }

    fun getNotificationList() = notificationDao.getAllNotifications()

    suspend fun getAllNotificationsFromApi(page:String,pageSize:String) = appApis.getAllNotifications(page, pageSize)

    suspend fun fetchDeviceConfigurationFromServer(){
        val id  = prefManager.getDeviceConfigId()
        if (id!=null) {
            appApis.getDeviceConfigurationDetails(id.toString()).onSuccess {
                prefManager.saveDeviceConfigurationDetails(it)
            }.onError { code, message ->
                Log.d("TAG", "fetchDeviceConfigurationFromServer:error "+message)
            }.onException {
                Log.d("TAG", "fetchDeviceConfigurationFromServer: exception"+it.localizedMessage)

            }
        }

    }

    fun fetchDeviceConfiguration():DeviceConfigurationResponse?
    {
        return prefManager.getDeviceConfigurationDetails()
    }


    suspend fun getAttendanceList(date:String): NetworkResult<List<AttendanceListResponseItem>> {
        return appApis.getAllAttendance(date)
    }

    fun getLast5LocationListFromDB() = locationDao.getLast5Items()

    suspend fun configureDevice(
        deviceType: Int,
        expiryDate: String,
        licenseKey: String,
        schoolId: Int,
        vehicleId: String,
        modelNo: String,
        deviceId: String,
        startDate: String,
        serialNo: String,
        bluetoothId: String
    ): NetworkResult<BasicMessageResponse> {

        return appApis.configureDevice(ConfigureDeviceRequest(
            deviceType = deviceType,
            expiryDate = expiryDate,
            licenseKey = licenseKey,
            schoolId = schoolId,
            vehicleId = vehicleId,
            modelNo = modelNo,
            deviceId = deviceId,
            startDate = startDate,
            serialNo = serialNo,
            bluetoothId = bluetoothId
        ))
    }

    fun saveDeviceConfigId(id:String){
        prefManager.saveDeviceConfigId(id)
    }

    suspend fun addAttendanceToDatabase(attendance: Attendance)
    {
        attendanceDao.addAttendance(attendance)
    }

    suspend fun syncAllAttendanceToServer()
    {
        val list = attendanceDao.getAllUnSyncedAttendances()
        val attendanceList = arrayListOf<CreateAttendanceRequest>()
        list.forEach { attendance: Attendance ->
            attendanceList.add(attendance.toCreateAttendanceRequest(fetchDeviceConfiguration()?.schoolId?:-1))
        }
        appApis.addAttendance(
            attendanceList
        ).onSuccess {
         attendanceDao.updateIsSyncByOutTime(true)
        }
    }

    suspend fun saveAttendanceToDatabase(attendanceModel: AttendanceModel){

    }

    suspend fun createRoom(roomName: String) : NetworkResult<RoomCreationResponseModel> {
        //val roomName = roomName +"_"+ prefManager.getDeviceConfigurationDetails()?.vehicleId
        val roomName = roomName +"_vehicleId"
        val createRoomRequestModel = CreateRoomRequestModel(roomName)
         return appApis.createRoom("https://68.178.160.179:8080/createRoom",createRoomRequestModel)
    }

    suspend fun createToken(createTokenRequestModel: CreateTokenRequestModel) : NetworkResult<CreateTokenResponseModel> {
        return appApis.createToken("https://68.178.160.179:8080/token",createTokenRequestModel)
    }
}