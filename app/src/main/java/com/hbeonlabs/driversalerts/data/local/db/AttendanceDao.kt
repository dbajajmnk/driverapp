package com.hbeonlabs.driversalerts.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hbeonlabs.driversalerts.data.local.db.models.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert
    suspend fun addAttendance(attendance: Attendance)

    @Query("SELECT * FROM attendance WHERE date = :date AND tagID = :tagId AND dayTime = :dayTime LIMIT 1")
    suspend fun getStudentByDateAndTagIdAndDayTime(date:String,tagId:String, dayTime:Int): Attendance?

    @Query("SELECT * FROM attendance WHERE isSync = 0")
    suspend fun getAllUnSyncedAttendances(): Flow<List<Attendance>>

    @Query("UPDATE attendance SET outTime = :newOutTime WHERE tagID = :tagID AND dayTime = :dayTime AND date = :date")
    suspend fun updateOutTimeByTagIDAndDayTime(tagID: String, dayTime: Int, newOutTime: String,date:String)

    @Query("DELETE FROM Attendance WHERE isSync = 1")
    suspend fun deleteSyncedAttendances()

    @Query("UPDATE Attendance SET isSync = :synced WHERE outTime != ''")
    suspend fun updateIsSyncByOutTime(synced: Boolean)




}