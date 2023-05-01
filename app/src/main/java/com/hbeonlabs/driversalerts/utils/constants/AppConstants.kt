package com.hbeonlabs.driversalerts.utils.constants

object AppConstants {
    const val FILE_PERMISSION_REQ_CODE = 121
    const val TEST = ""
    const val CAMERA_PERMISSION_REQ_CODE = 1111
    const val LOCATION_PERMISSION_REQ_CODE = 2222
    const val BLUETOOTH_PERMISSION_REQ_CODE = 3333

    const val START_HOUR = 8
    const val START_MINUTES = 0
    const val END_HOUR= 23
    const val END_MINUTES= 49

    const val OVERSPEEDING_MESSAGE = "You are over speeding. Please slow down"
    const val DROWSINESS_MESSAGE = "Are you feeling drowsy. Please stop and fresh up"

    const val OVERSPEEDING_THRESHOLD = 30f

    enum class NotificationType{
        LOG,WARNING
    }

    enum class NotificationSubType{
        RASHDRIVING,OVERSPEEDING,SEATBELT,DROWSNISS
    }


}