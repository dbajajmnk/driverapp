package com.hbeonlabs.driversalerts.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationAndSpeedDao {

    @Query("SELECT * FROM locationandspeed")
    fun getAllLocationAndSpeedData(): Flow<List<LocationAndSpeed>>

    @Query("SELECT * FROM locationandspeed ORDER BY timeInMills DESC LIMIT 3")
    fun getLast5Items():Flow<List<LocationAndSpeed>>

    @Insert
    suspend fun addData(chat:LocationAndSpeed)
}