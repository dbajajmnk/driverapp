package com.hbeonlabs.driversalerts.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM Warning")
    fun getAllNotifications(): Flow<List<Warning>>

    @Insert
    suspend fun addNotification(appNotification: Warning)

}