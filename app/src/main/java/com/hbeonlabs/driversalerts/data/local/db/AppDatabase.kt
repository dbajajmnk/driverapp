package com.hbeonlabs.driversalerts.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hbeonlabs.driversalerts.data.local.db.models.LocationAndSpeed

@Database(
    entities = [
        LocationAndSpeed::class
               ],
    version = 1
)
abstract class AppDatabase :RoomDatabase(){
    abstract fun getLocationAndSpeedDao(): LocationAndSpeedDao
}