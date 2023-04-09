package com.hbeonlabs.driversalerts.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hbeonlabs.driversalerts.data.local.db.models.Attendance
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query("SELECT * FROM attendance")
    fun getAllWarnings(): Flow<List<Attendance>>

    @Insert
    suspend fun addAttendance(attendance: Attendance)

}