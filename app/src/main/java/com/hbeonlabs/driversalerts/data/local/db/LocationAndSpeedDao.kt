package com.hbeonlabs.driversalerts.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationAndSpeedDao {

    @Query("SELECT * FROM locationandspeed")
    fun getAllCommunityChat(): Flow<List<LocationAndSpeed>>

    @Insert
    suspend fun addData(chat:LocationAndSpeed)
}