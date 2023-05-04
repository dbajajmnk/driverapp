package com.hbeonlabs.driversalerts.data.mappers

import com.hbeonlabs.driversalerts.bluetooth.AttendanceModel
import com.hbeonlabs.driversalerts.data.local.db.models.Attendance
import com.hbeonlabs.driversalerts.data.remote.request.CreateAttendanceRequest

fun Attendance.toCreateAttendanceRequest(schoolId:Int):CreateAttendanceRequest{
    return CreateAttendanceRequest(
        studentId = tagID.toInt(),
        date = date,
        inTime = inTime,
        schoolId = schoolId,
        isPresent = 1,
        outTime = outTime
    )
}