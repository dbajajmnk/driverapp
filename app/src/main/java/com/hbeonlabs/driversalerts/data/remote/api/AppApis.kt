package com.hbeonlabs.driversalerts.data.remote.api

import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.remote.request.DeviceConfigurationRequest
import com.hbeonlabs.driversalerts.data.remote.response.AttendanceListResponse
import com.hbeonlabs.driversalerts.data.remote.response.BasicMessageResponse
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import com.hbeonlabs.driversalerts.data.remote.response.NotificationResponse
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.CONFIGURE_DEVICE
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.CREATE_ATTENDANCE
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.GET_ALL_ATTENDANCE
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.GET_ALL_RECORDINGS
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.GET_DEVICE_CONFIGURATION
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.GET_NOTIFICATIONS
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.POST_NOTIFICATION
import com.hbeonlabs.driversalerts.utils.network.NetworkResult
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AppApis {

    @GET("$GET_DEVICE_CONFIGURATION/{device_id}")
    suspend fun getDeviceConfigurationDetails(@Path("device_id") id:String): NetworkResult<DeviceConfigurationResponse>

    @GET(GET_NOTIFICATIONS)
    suspend fun getAllNotifications(): NetworkResult<NotificationResponse>

    // Date Format =  2023-01-09 = YYYY-MM-DD
    @GET("$GET_ALL_ATTENDANCE/{date}")
    suspend fun getAllAttendance(@Path("date") date:String): NetworkResult<AttendanceListResponse>

    @GET(GET_ALL_RECORDINGS)
    suspend fun getAllRecordings(): NetworkResult<NotificationResponse>

    // todo Request body
    @POST(POST_NOTIFICATION)
    suspend fun sendNotificationData(): NetworkResult<BasicMessageResponse>

    // todo Request body
    @POST(CONFIGURE_DEVICE)
    suspend fun configureDevice(@Body deviceConfigurationRequest: DeviceConfigurationRequest): NetworkResult<BasicMessageResponse>

    //todo request body needed
    @POST(CREATE_ATTENDANCE)
    suspend fun addAttendance(@Body attendance: AttendanceModel):NetworkResult<BasicMessageResponse>




}