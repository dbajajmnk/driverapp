package com.hbeonlabs.driversalerts.bluetooth

interface AttendanceCallback {
    fun onAttendance(attendanceModel: AttendanceModel)
    fun onConnect(status: Boolean)
}
