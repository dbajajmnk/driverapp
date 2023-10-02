package com.hbeonlabs.driversalerts.data.remote.api

import com.hbeonlabs.driversalerts.data.remote.request.ConfigureDeviceRequest
import com.hbeonlabs.driversalerts.data.remote.request.CreateAttendanceRequest
import com.hbeonlabs.driversalerts.data.remote.request.CreateNotificationDTO
import com.hbeonlabs.driversalerts.data.remote.request.CreateRoomRequestModel
import com.hbeonlabs.driversalerts.data.remote.request.CreateTokenRequestModel
import com.hbeonlabs.driversalerts.data.remote.response.AttendanceListResponseItem
import com.hbeonlabs.driversalerts.data.remote.response.BasicMessageResponse
import com.hbeonlabs.driversalerts.data.remote.response.CreateTokenResponseModel
import com.hbeonlabs.driversalerts.data.remote.response.DeviceConfigurationResponse
import com.hbeonlabs.driversalerts.data.remote.response.NotificationResponse
import com.hbeonlabs.driversalerts.data.remote.response.RoomCreationResponseModel
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.CONFIGURE_DEVICE
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.CREATE_ATTENDANCE
import com.hbeonlabs.driversalerts.utils.constants.EndPoints.CREATE_ROOM
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
import retrofit2.http.Query
import retrofit2.http.Url

interface AppApis {

    @GET("$GET_DEVICE_CONFIGURATION/{device_id}")
    suspend fun getDeviceConfigurationDetails(@Path("device_id") id:String): NetworkResult<DeviceConfigurationResponse>

    @GET(GET_NOTIFICATIONS)
    suspend fun getAllNotifications(@Query("page") page:String, @Query("pageSize")pageSize:String): NetworkResult<NotificationResponse>

    // Date Format =  2023-01-09 = YYYY-MM-DD
    @GET("$GET_ALL_ATTENDANCE/{date}")
    suspend fun getAllAttendance(@Path("date") date:String): NetworkResult<List<AttendanceListResponseItem>>

    @GET(GET_ALL_RECORDINGS)
    suspend fun getAllRecordings()

    @POST(POST_NOTIFICATION)
    suspend fun sendNotificationData(@Body createNotificationDTO: CreateNotificationDTO): NetworkResult<BasicMessageResponse>

    @POST(CONFIGURE_DEVICE)
    suspend fun configureDevice(@Body deviceConfigurationRequest: ConfigureDeviceRequest): NetworkResult<BasicMessageResponse>

    @POST(CREATE_ATTENDANCE)
    suspend fun addAttendance(@Body attendance: List<CreateAttendanceRequest>):NetworkResult<BasicMessageResponse>

    @POST(CREATE_ROOM)
    suspend fun createRoom(@Body createRoomRequestModel: CreateRoomRequestModel):NetworkResult<RoomCreationResponseModel>

    @POST()
    suspend fun createToken(@Url url : String,@Body createTokenRequestModel: CreateTokenRequestModel):NetworkResult<CreateTokenResponseModel>

}