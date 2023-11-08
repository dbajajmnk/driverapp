package com.hbeonlabs.driversalerts.utils.constants

object AppConstants {
    const val FILE_PERMISSION_REQ_CODE = 121
    const val TEST = ""
    const val CAMERA_PERMISSION_REQ_CODE = 1111
    const val LOCATION_PERMISSION_REQ_CODE = 2222
    const val BLUETOOTH_PERMISSION_REQ_CODE = 3333
    const val WAKE_LOCK_PERMISSION_REQ_CODE = 4444

    const val START_HOUR = 8
    const val START_MINUTES = 0
    const val END_HOUR= 23
    const val END_MINUTES= 49

    const val RFID_CONNECTION_SUCCESS_MESSAGE = "Connected with RFID device successfully"
    const val RFID_CONNECTION_FAIL_MESSAGE = "Could not be connected with RFID device"
    const val OVERSPEEDING_MESSAGE = "You are over speeding. Please slow down"
    const val DROWSINESS_MESSAGE = "Are you feeling drowsy. Please stop and fresh up"
    const val STEAMING_START_MESSAGE = "Streaming started"
    const val STEAMING_STOP_MESSAGE = "Streaming stoped"

    const val OVERSPEEDING_THRESHOLD = 80f

    enum class NotificationType{
        LOG,WARNING
    }

    enum class NotificationSubType{
        RASHDRIVING,OVERSPEEDING,SEATBELT,DROWSNISS, RFID_CONNECTION, STREAMING_START, STREAMING_STOP
    }


    enum class AttendanceTime{
        MORNING,EVENING
    }

}