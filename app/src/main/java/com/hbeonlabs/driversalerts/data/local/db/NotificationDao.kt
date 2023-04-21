package com.hbeonlabs.driversalerts.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hbeonlabs.driversalerts.data.local.db.models.Notification
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification")
    fun getAllNotifications(): Flow<List<Notification>>

    @Insert
    suspend fun addNotification(warning: Notification)

}