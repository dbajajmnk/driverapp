package com.hbeonlabs.driversalerts.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed
import com.hbeonlabs.driversalerts.data.local.db.models.Warning
import kotlinx.coroutines.flow.Flow

@Dao
interface WarningsDao {

    @Query("SELECT * FROM warning")
    fun getAllWarnings(): Flow<List<Warning>>

    @Insert
    suspend fun addWarning(warning: Warning)

}